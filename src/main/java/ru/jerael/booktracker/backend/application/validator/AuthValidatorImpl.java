package ru.jerael.booktracker.backend.application.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.jerael.booktracker.backend.domain.constant.EmailVerificationRules;
import ru.jerael.booktracker.backend.domain.exception.ValidationException;
import ru.jerael.booktracker.backend.domain.exception.factory.CommonValidationErrorFactory;
import ru.jerael.booktracker.backend.domain.exception.model.ValidationError;
import ru.jerael.booktracker.backend.domain.model.auth.ConfirmRegistration;
import ru.jerael.booktracker.backend.domain.model.auth.RefreshTokenPayload;
import ru.jerael.booktracker.backend.domain.model.auth.UserLogin;
import ru.jerael.booktracker.backend.domain.validator.AuthValidator;
import ru.jerael.booktracker.backend.domain.validator.FieldValidator;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthValidatorImpl implements AuthValidator {
    private final FieldValidator fieldValidator;

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

    @Override
    public void validateLogin(UserLogin data) {
        List<ValidationError> errors = new ArrayList<>();
        errors.addAll(fieldValidator.validateEmail(data.email()));
        errors.addAll(fieldValidator.validatePassword(data.password()));
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    @Override
    public void validateRefreshTokenPayload(RefreshTokenPayload data) {
        List<ValidationError> errors = new ArrayList<>();
        String refreshToken = data.refreshToken();

        if (refreshToken == null || refreshToken.isBlank()) {
            errors.add(CommonValidationErrorFactory.emptyField("refreshToken"));
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
