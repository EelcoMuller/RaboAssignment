package nl.ordina.raboservice;

import nl.ordina.raboservice.converters.SecurityConverter;
import nl.ordina.raboservice.model.PaymentAcceptedResponse;
import nl.ordina.raboservice.model.PaymentInitiationRequest;
import nl.ordina.raboservice.model.PaymentRejectedResponse;
import nl.ordina.raboservice.model.PaymentResponse;
import nl.ordina.raboservice.validators.RequestValidations;
import nl.ordina.raboservice.validators.SecurityValidations;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPrivateCrtKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PaymentServiceControllerTest {
    @Mock
    private SecurityConverter securityConverter;

    @Mock
    private SecurityValidations securityValidations;

    @Mock
    private RequestValidations requestValidations;

    @InjectMocks
    private PaymentController controller;

    @BeforeEach()
    void setup() throws Exception {
        X509Certificate certificate = mock(X509Certificate.class);
        when(securityConverter.getCertificate()).thenReturn(certificate);
        when(securityConverter.getPrivateKeyOverride(any())).thenReturn(mock(BCRSAPrivateCrtKey.class));
        when(certificate.getPublicKey()).thenReturn(mock(PublicKey.class));
        when(securityConverter.createsignature(any(), any(), any())).thenReturn("TEST");
    }

    @Test
    void unknownCertifcateGives400ErrorCode() {
        when(securityValidations.testCommonNameWhitelisted(any())).thenReturn(false);
        ResponseEntity<PaymentResponse> result = controller.initiatePayment(null, null, null, null);
        assertEquals(400, result.getStatusCodeValue());
        assertTrue(result.getHeaders().containsKey("Signature-Certificate"));
        assertTrue(result.getHeaders().containsKey("Signature"));
    }

    @Test
    void wrongSignatureGives400ErrorCode() throws Exception {
        when(securityValidations.testCommonNameWhitelisted(any())).thenReturn(true);
        when(securityValidations.verifySig(any(), any(), any(), any())).thenReturn(false);
        PaymentInitiationRequest request = new PaymentInitiationRequest("", "", "", null, null);
        ResponseEntity<PaymentResponse> result = controller.initiatePayment("", "", "", request);
        assertEquals(400, result.getStatusCodeValue());
        assertTrue(result.getHeaders().containsKey("Signature-Certificate"));
        assertTrue(result.getHeaders().containsKey("Signature"));
        assertEquals("INVALID_SIGNATURE", ((PaymentRejectedResponse) result.getBody()).getReasonCode().name());
    }

    @Test
    void wrongRequestDataGives422ErrorCode() throws Exception {
        when(securityValidations.testCommonNameWhitelisted(any())).thenReturn(true);
        when(securityValidations.verifySig(any(), any(), any(), any())).thenReturn(true);
        List<String> messages = new ArrayList<>();
        messages.add("message");
        when(requestValidations.validateRequest(any())).thenReturn(messages);
        PaymentInitiationRequest request = new PaymentInitiationRequest("", "", "", null, null);
        ResponseEntity<PaymentResponse> result = controller.initiatePayment("", "", "", request);
        assertEquals(422, result.getStatusCodeValue());
        assertTrue(result.getHeaders().containsKey("Signature-Certificate"));
        assertTrue(result.getHeaders().containsKey("Signature"));
        assertEquals("INVALID_REQUEST", ((PaymentRejectedResponse) result.getBody()).getReasonCode().name());
    }

    @Test
    void noErrorsLeadToResponse201() throws Exception {
        when(securityValidations.testCommonNameWhitelisted(any())).thenReturn(true);
        when(securityValidations.verifySig(any(), any(), any(), any())).thenReturn(true);
        List<String> messages = new ArrayList<>();
        when(requestValidations.validateRequest(any())).thenReturn(messages);
        PaymentInitiationRequest request = new PaymentInitiationRequest("", "", "", null, null);
        ResponseEntity<PaymentResponse> result = controller.initiatePayment("", "", "", request);
        assertEquals(201, result.getStatusCodeValue());
        assertTrue(result.getHeaders().containsKey("Signature-Certificate"));
        assertTrue(result.getHeaders().containsKey("Signature"));
        assertEquals("Accepted", ((PaymentAcceptedResponse) result.getBody()).getStatus().name());
    }
}
