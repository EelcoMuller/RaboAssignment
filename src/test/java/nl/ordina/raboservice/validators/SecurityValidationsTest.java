package nl.ordina.raboservice.validators;

import nl.ordina.raboservice.converters.SecurityConverter;
import org.junit.jupiter.api.Test;

import java.security.cert.X509Certificate;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SecurityValidationsTest {

    private SecurityValidations securityValidations = new SecurityValidations();

    @Test
    public void testWhiteListCorrect() {
        SecurityConverter securityConverter = new SecurityConverter();
        X509Certificate cert = securityConverter.getCertificate();
        assertTrue(securityValidations.testCommonNameWhitelisted(cert));
    }
}
