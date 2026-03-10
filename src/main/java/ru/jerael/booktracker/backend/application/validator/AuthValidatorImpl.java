package ru.jerael.booktracker.backend.application.validator;

import org.springframework.stereotype.Component;
import ru.jerael.booktracker.backend.domain.constant.EmailVerificationRules;
import ru.jerael.booktracker.backend.domain.exception.ValidationException;
import ru.jerael.booktracker.backend.domain.exception.factory.CommonValidationErrorFactory;
import ru.jerael.booktracker.backend.domain.exception.model.ValidationError;
import ru.jerael.booktracker.backend.domain.model.auth.ConfirmRegistration;
import ru.jerael.booktracker.backend.domain.validator.AuthValidator;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class AuthValidatorImpl implements AuthValidator {
    @Override
    public void validateRegistrationConfirmation(ConfirmRegistration data) {
        List<ValidationError> errors = new ArrayList<>();
        UUID userId = data.userId();
        String token = data.token();

        if (userId == null) {
            errors.add(CommonValidationErrorFactory.emptyField("userId"));
        }

        if (token == null || token.isBlank()) {
            errors.add(CommonValidationErrorFactory.emptyField("token"));
        } else {
            if (token.length() < EmailVerificationRules.TOKEN_MIN_LENGTH) {
                errors.add(
                    CommonValidationErrorFactory.fieldTooShort("token", EmailVerificationRules.TOKEN_MIN_LENGTH));
            }
            if (token.length() > EmailVerificationRules.TOKEN_MAX_LENGTH) {
                errors.add(
                    CommonValidationErrorFactory.fieldTooLong("token", EmailVerificationRules.TOKEN_MAX_LENGTH));
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
