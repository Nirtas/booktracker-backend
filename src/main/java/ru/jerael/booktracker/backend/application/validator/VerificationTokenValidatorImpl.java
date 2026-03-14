package ru.jerael.booktracker.backend.application.validator;

import org.springframework.stereotype.Component;
import ru.jerael.booktracker.backend.domain.exception.ValidationException;
import ru.jerael.booktracker.backend.domain.exception.factory.EmailVerificationErrorFactory;
import ru.jerael.booktracker.backend.domain.exception.model.ValidationError;
import ru.jerael.booktracker.backend.domain.model.verification.EmailVerification;
import ru.jerael.booktracker.backend.domain.validator.VerificationTokenValidator;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class VerificationTokenValidatorImpl implements VerificationTokenValidator {
    public void validate(EmailVerification emailVerification, String token) {
        List<ValidationError> errors = new ArrayList<>();
        if (Instant.now().isAfter(emailVerification.expiresAt())) {
            errors.add(EmailVerificationErrorFactory.tokenExpired());
        }
        if (!emailVerification.token().equals(token)) {
            errors.add(EmailVerificationErrorFactory.invalidToken());
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
