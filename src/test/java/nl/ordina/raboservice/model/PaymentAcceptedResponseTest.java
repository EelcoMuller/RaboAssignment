package nl.ordina.raboservice.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PaymentAcceptedResponseTest {

    @Test
    void testPaymentAcceptedResponseCorrectlyInitiated() {
        PaymentInitiationRequest initiationRequest = new PaymentInitiationRequest("xx", "yy", "1.00", null, null);
        assertNotNull(initiationRequest);
    }
}
