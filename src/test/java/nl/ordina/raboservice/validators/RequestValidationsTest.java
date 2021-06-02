package nl.ordina.raboservice.validators;

import nl.ordina.raboservice.model.PaymentInitiationRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RequestValidationsTest {
    private RequestValidations requestValidations = new RequestValidations();

    @Test
    void testBeanValidation() {

        PaymentInitiationRequest request = new PaymentInitiationRequest(null, null, null, null, null);
        List<String> messages = requestValidations.validateRequest(request);
        assertEquals(messages.size(), 3);

    }

    @Test
    void testAccountLimitExceededFalse() {
        PaymentInitiationRequest request = new PaymentInitiationRequest("NL02RABO7134384551", "NL94ABNA1008270121", "1.00", null, null);
        assertFalse(requestValidations.testIfAccountLimitExceeded(request));
    }

    @Test
    void testAccountLimitExceededTrue() {
        PaymentInitiationRequest request = new PaymentInitiationRequest("NL00RABO000", "NL94ABNA1008270121", "1.00", null, null);
        assertTrue(requestValidations.testIfAccountLimitExceeded(request));
    }

}
