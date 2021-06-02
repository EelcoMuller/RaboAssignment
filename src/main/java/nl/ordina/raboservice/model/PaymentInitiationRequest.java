package nl.ordina.raboservice.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


public class PaymentInitiationRequest {
    @NotNull
    @Pattern(message = "not a correct iban format", regexp = "[A-Z]{2}[0-9]{2}[a-zA-Z0-9]{1,30}")
    private String debtorIBAN;

    @NotNull
    @Pattern(message = "not a correct iban format", regexp = "[A-Z]{2}[0-9]{2}[a-zA-Z0-9]{1,30}")
    private String creditorIBAN;

    @NotNull
    @Pattern(message = "not a correct amount", regexp = "-?[0-9]+(\\.[0-9]{1,3})?")
    private String amount;

    @Pattern(message = "not a correct currency", regexp = "[A-Z]{3}")
    private String currency;
    private String endToEndId;

    public PaymentInitiationRequest(String debtorIBAN, String creditorIBAN, String amount, String currency, String endToEndId) {
        this.debtorIBAN = debtorIBAN;
        this.creditorIBAN = creditorIBAN;
        this.amount = amount;
        this.currency = currency;
        this.endToEndId = endToEndId;
    }

    public String getDebtorIBAN() {
        return debtorIBAN;
    }

    public String getCreditorIBAN() {
        return creditorIBAN;
    }

    public String getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getEndToEndId() {
        return endToEndId;
    }


}
