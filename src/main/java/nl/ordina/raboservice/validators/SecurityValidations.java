package nl.ordina.raboservice.validators;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import nl.ordina.raboservice.model.PaymentInitiationRequest;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.X509Certificate;

@Service
public class SecurityValidations {

    public boolean testCommonNameWhitelisted(X509Certificate certificate) {
        return certificate.getSubjectDN().getName().contains("CN=Sandbox-TPP");
    }

    public boolean verifySig(PublicKey key, byte[] sig, String xRequestId, PaymentInitiationRequest request) throws Exception {

        ObjectWriter ow = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).writer();
        String json = ow.writeValueAsString(request);

        System.out.println(json);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedRequest = digest.digest(
                json.getBytes());

        byte[] data = (xRequestId + encodedRequest).getBytes();

        Signature signer = Signature.getInstance("SHA256withRSA");
        signer.initVerify(key);
        signer.update(data);
        return (signer.verify(Base64.decode(sig)));
        //this seems not to validate, no idea why
    }
}
