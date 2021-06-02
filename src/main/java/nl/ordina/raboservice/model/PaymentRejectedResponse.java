package nl.ordina.raboservice.model;

public class PaymentRejectedResponse extends PaymentResponse {
    private TransactionStatus status;
    private String reason;

    private ErrorReasonCode reasonCode;

    public PaymentRejectedResponse(TransactionStatus status, String reason, ErrorReasonCode reasonCode) {
        this.status = status;
        this.reason = reason;
        this.reasonCode = reasonCode;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public ErrorReasonCode getReasonCode() {
        return reasonCode;
    }
}
