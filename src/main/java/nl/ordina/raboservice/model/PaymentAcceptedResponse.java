package nl.ordina.raboservice.model;

public class PaymentAcceptedResponse extends PaymentResponse {
    private String paymentId;
    private TransactionStatus status;

    public PaymentAcceptedResponse(String paymentId, TransactionStatus status) {
        this.paymentId = paymentId;
        this.status = status;

    }

    public String getPaymentId() {
        return paymentId;
    }

    public TransactionStatus getStatus() {
        return status;
    }

}
