package nl.ordina.raboservice.converters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.nimbusds.jose.util.X509CertUtils;
import nl.ordina.raboservice.model.PaymentResponse;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPrivateCrtKey;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.stream.Collectors;

@Service
public class SecurityConverter {
    public X509Certificate getCertificate() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = loader.getResourceAsStream("payment.cert");

        String pemEncodedCert = new BufferedReader(
                new InputStreamReader(inputStream)).lines()
                .collect(Collectors.joining("\n"));

        return X509CertUtils.parse(pemEncodedCert);
    }


    public BCRSAPrivateCrtKey getPrivateKey(String keyString) throws Exception {

        java.security.Security.addProvider(
                new org.bouncycastle.jce.provider.BouncyCastleProvider()
        );


        KeyFactory factory = KeyFactory.getInstance("RSA");
        String realPK = keyString.replaceAll("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("-----BEGIN RSA PRIVATE KEY-----", "")
                .replaceAll("\n", "");

        byte[] b1 = Base64.getDecoder().decode(realPK);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(b1);

        return (BCRSAPrivateCrtKey) factory.generatePrivate(spec);

    }

    public String createsignature(PrivateKey key, String xRequestId, PaymentResponse request) throws Exception {


        ObjectWriter ow = new ObjectMapper().writer();
        String json = ow.writeValueAsString(request);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedRequest = digest.digest(
                json.getBytes());

        byte[] data = (xRequestId + encodedRequest).getBytes();

        Signature signer = Signature.getInstance("SHA256withRSA");
        signer.initSign(key);
        signer.update(data);
        byte[] s = signer.sign();
        String returnValue = Base64.getEncoder().encodeToString(s);
        return returnValue;
    }

    public BCRSAPrivateCrtKey getPrivateKeyOverride(String signatureCertificate) throws Exception {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = loader.getResourceAsStream("test.rsa");

        String certificateString = new BufferedReader(
                new InputStreamReader(inputStream)).lines()
                .collect(Collectors.joining("\n"));
        return this.getPrivateKey(certificateString);
    }
}
