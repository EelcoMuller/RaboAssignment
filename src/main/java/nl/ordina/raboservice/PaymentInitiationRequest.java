package nl.ordina.raboservice;

// properties:
//         debtorIBAN:
//         $ref: '#/components/schemas/IBAN'
//         creditorIBAN:
//         $ref: '#/components/schemas/IBAN'
//         amount:
//         description: >-
//         Amount of the payment initiation
//         type: string
//         format: "-?[0-9]+(\\.[0-9]{1,3})?"
//         currency:
//         type: string
//         format: "[A-Z]{3}"
//default: EUR
//        endToEndId:
//        type: string
//        description: >-
//        Unique identifier per payment initiation request provided
//        by the client
public class PaymentInitiationRequest{
    private String debtorIBAN;
    private String creditorIBAN;
    private String amount;
    private String currency;
    private String endToEndId;
}
