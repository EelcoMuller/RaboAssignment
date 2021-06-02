package nl.ordina.raboservice.converters;

import nl.ordina.raboservice.model.ErrorReasonCode;
import nl.ordina.raboservice.model.PaymentRejectedResponse;
import nl.ordina.raboservice.model.PaymentResponse;
import nl.ordina.raboservice.model.TransactionStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.PrivateKey;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SecurityConverterTest {

    private SecurityConverter securityConverter = new SecurityConverter();

    @Test
    void testGetCertificate() {
        assertNotNull(securityConverter.getCertificate());
    }

    @Test
    void testGetPrivateKey() throws Exception {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = loader.getResourceAsStream("test.rsa");

        String certificateString = new BufferedReader(
                new InputStreamReader(inputStream)).lines()
                .collect(Collectors.joining("\n"));
        assertNotNull(securityConverter.getPrivateKey(certificateString));
    }

    @Test
    void testGetPrivateKeyGoesAlright() throws Exception {
        assertNotNull(securityConverter.getPrivateKeyOverride(""));
    }

    @Test
    void testcreateSignatureGivesResult() throws Exception {

        PrivateKey key = securityConverter.getPrivateKeyOverride("");
        String xRequestId = "req";
        PaymentResponse request = new PaymentRejectedResponse(TransactionStatus.Rejected, "", ErrorReasonCode.GENERAL_ERROR);
        String createsignature = securityConverter.createsignature(key, xRequestId, request);
        Assertions.assertTrue(createsignature.length() > 100);
    }


}
