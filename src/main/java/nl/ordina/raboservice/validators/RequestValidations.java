package nl.ordina.raboservice.validators;

import nl.ordina.raboservice.model.PaymentInitiationRequest;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RequestValidations {
    public List<String> validateRequest(PaymentInitiationRequest request) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<PaymentInitiationRequest>> violations = validator.validate(request);
        return violations.stream().map(v -> v.getMessage()).collect(Collectors.toList());
    }

    //Amount > 0 && Sum(DebtorAccountIBAN) mod Length(DebtorAccountIBAN) == 0
    public boolean testIfAccountLimitExceeded(PaymentInitiationRequest request) {
        String debtorIban = request.getDebtorIBAN();
        String nummericForSum = debtorIban.replaceAll("[^0-9]", "");
        int sum = 0;

        for (int i = 0; i < nummericForSum.length(); i++) {
            sum = sum + Character.getNumericValue(nummericForSum.charAt(i));
        }

        double amount = Double.parseDouble(request.getAmount());

        return (amount > 0 && (sum % debtorIban.length() == 0));
    }
}
