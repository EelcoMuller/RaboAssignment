package nl.ordina.raboservice;

import nl.ordina.raboservice.converters.SecurityConverter;
import nl.ordina.raboservice.model.*;
import nl.ordina.raboservice.validators.RequestValidations;
import nl.ordina.raboservice.validators.SecurityValidations;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPrivateCrtKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private SecurityConverter securityConverter;

    @Autowired
    private SecurityValidations securityValidations;

    @Autowired
    private RequestValidations requestValidations;

    @GetMapping("/test")
    public String greeting() {
        return "OK";
    }

    //there something I don't see why the signature validation doesnt work all
    //is implemented with the boolean it's possible to test the functionality.
    private final boolean SKIP_SECURITY = false;

    @PutMapping("/initiatePayment")
    public ResponseEntity<PaymentResponse> initiatePayment(@RequestHeader(name = "X-Request-Id") String requestId,
                                                           @RequestHeader(name = "Signature-Certificate") String signatureCertificate,
                                                           @RequestHeader(name = "Signature") String signature,
                                                           @RequestBody PaymentInitiationRequest paymentInitiationRequest) {

        try {
            X509Certificate certificate = securityConverter.getCertificate();
            BCRSAPrivateCrtKey privateKey = securityConverter.getPrivateKeyOverride(signatureCertificate);
            PublicKey publicKey = certificate.getPublicKey();
            if (!securityValidations.testCommonNameWhitelisted(certificate)) {
                PaymentResponse response = new PaymentRejectedResponse(TransactionStatus.Rejected, "", ErrorReasonCode.UNKNOWN_CERTIFICATE);
                HttpHeaders headers = getHttpHeaders(requestId, signatureCertificate, privateKey, response);
                return ResponseEntity.status(400).headers(headers).body(response);
            }

            if (!securityValidations.verifySig(publicKey, signature.getBytes(), requestId, paymentInitiationRequest) && !SKIP_SECURITY) {
                PaymentResponse response = new PaymentRejectedResponse(TransactionStatus.Rejected, "", ErrorReasonCode.INVALID_SIGNATURE);
                HttpHeaders headers = getHttpHeaders(requestId, signatureCertificate, privateKey, response);
                return ResponseEntity.status(400).headers(headers).body(response);
            }

            List<String> messages = requestValidations.validateRequest(paymentInitiationRequest);

            if (messages.size() > 0) {
                String reason = messages.stream().collect(Collectors.joining(","));
                PaymentResponse response = new PaymentRejectedResponse(TransactionStatus.Rejected, reason, ErrorReasonCode.INVALID_REQUEST);
                HttpHeaders headers = getHttpHeaders(requestId, signatureCertificate, privateKey, response);
                return ResponseEntity.status(422).headers(headers).body(response);
            }

            if (requestValidations.testIfAccountLimitExceeded(paymentInitiationRequest)) {
                String reason = "account limit exceeded";
                PaymentResponse response = new PaymentRejectedResponse(TransactionStatus.Rejected, reason, ErrorReasonCode.LIMIT_EXCEEDED);
                return ResponseEntity.status(422).body(response);
            }

            PaymentResponse response = new PaymentAcceptedResponse(UUID.randomUUID().toString(), TransactionStatus.Accepted);
            HttpHeaders headers = getHttpHeaders(requestId, signatureCertificate, privateKey, response);
            return ResponseEntity.status(201).headers(headers).body(response);

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        }
    }

    private HttpHeaders getHttpHeaders(String requestId, String signatureCertificate, BCRSAPrivateCrtKey privateKey, PaymentResponse response) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Signature-Certificate", signatureCertificate);
        headers.add("Signature", securityConverter.createsignature(privateKey, requestId, response));
        return headers;
    }
}
