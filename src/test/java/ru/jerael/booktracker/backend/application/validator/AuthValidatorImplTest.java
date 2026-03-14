package ru.jerael.booktracker.backend.application.validator;

import org.junit.jupiter.api.Test;
import ru.jerael.booktracker.backend.domain.constant.EmailVerificationRules;
import ru.jerael.booktracker.backend.domain.exception.ValidationException;
import ru.jerael.booktracker.backend.domain.exception.code.CommonValidationErrorCode;
import ru.jerael.booktracker.backend.domain.model.auth.ConfirmRegistration;
import ru.jerael.booktracker.backend.domain.model.auth.LogoutPayload;
import ru.jerael.booktracker.backend.domain.model.auth.RefreshTokenPayload;
import ru.jerael.booktracker.backend.domain.model.auth.ResendVerification;
import ru.jerael.booktracker.backend.domain.model.verification.VerificationType;
import ru.jerael.booktracker.backend.domain.validator.AuthValidator;
import ru.jerael.booktracker.backend.domain.validator.FieldValidator;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthValidatorImplTest {
    private final FieldValidator fieldValidator = new FieldValidatorImpl();
    private final AuthValidator authValidator = new AuthValidatorImpl(fieldValidator);

    private final UUID userId = UUID.fromString("2c5781ea-1bc2-4561-a83d-26106df2526e");
    private final String refreshToken = "refresh token";

    @Test
    void validateRegistrationConfirmation_WhenValid_ShouldNotThrowException() {
        ConfirmRegistration data = new ConfirmRegistration(userId, "123456");

        assertDoesNotThrow(() -> authValidator.validateRegistrationConfirmation(data));
    }

    @Test
    void validateRegistrationConfirmation_WhenUserIdIsNull_ShouldThrowException() {
        ConfirmRegistration data = new ConfirmRegistration(null, "123456");

        ValidationException ex =
            assertThrows(ValidationException.class, () -> authValidator.validateRegistrationConfirmation(data));

        assertThat(ex.getErrors()).anyMatch(e -> e.field().equals("userId"));
    }

    @Test
    void validateRegistrationConfirmation_WhenTokenIsBlank_ShouldThrowException() {
        ConfirmRegistration data = new ConfirmRegistration(userId, "  ");

        ValidationException ex =
            assertThrows(ValidationException.class, () -> authValidator.validateRegistrationConfirmation(data));

        assertThat(ex.getErrors()).anyMatch(e -> e.field().equals("token"));
    }

    @Test
    void validateRegistrationConfirmation_WhenTokenTooShort_ShouldThrowException() {
        ConfirmRegistration data = new ConfirmRegistration(userId, "1");

        ValidationException ex =
            assertThrows(ValidationException.class, () -> authValidator.validateRegistrationConfirmation(data));

        assertThat(ex.getErrors()).anyMatch(e -> e.code().equals(CommonValidationErrorCode.FIELD_TOO_SHORT.name()));
    }

    @Test
    void validateRegistrationConfirmation_WhenTokenTooLong_ShouldThrowException() {
        String token = "a".repeat(EmailVerificationRules.TOKEN_MAX_LENGTH + 1);
        ConfirmRegistration data = new ConfirmRegistration(userId, token);

        ValidationException ex =
            assertThrows(ValidationException.class, () -> authValidator.validateRegistrationConfirmation(data));

        assertThat(ex.getErrors()).anyMatch(e -> e.code().equals(CommonValidationErrorCode.FIELD_TOO_LONG.name()));
    }

    @Test
    void validateRefreshTokenPayload_WhenValid_ShouldNotThrowException() {
        RefreshTokenPayload data = new RefreshTokenPayload(refreshToken);

        assertDoesNotThrow(() -> authValidator.validateRefreshTokenPayload(data));
    }

    @Test
    void validateRefreshTokenPayload_WhenRefreshTokenIsBlank_ShouldThrowException() {
        RefreshTokenPayload data = new RefreshTokenPayload("  ");

        ValidationException ex =
            assertThrows(ValidationException.class, () -> authValidator.validateRefreshTokenPayload(data));

        assertThat(ex.getErrors()).anyMatch(e -> e.field().equals("refreshToken"));
    }

    @Test
    void validateLogoutPayload_WhenValid_ShouldNotThrowException() {
        LogoutPayload data = new LogoutPayload(refreshToken);

        assertDoesNotThrow(() -> authValidator.validateLogoutPayload(data));
    }

    @Test
    void validateLogoutPayload_WhenRefreshTokenIsBlank_ShouldThrowException() {
        LogoutPayload data = new LogoutPayload("  ");

        ValidationException ex =
            assertThrows(ValidationException.class, () -> authValidator.validateLogoutPayload(data));

        assertThat(ex.getErrors()).anyMatch(e -> e.field().equals("refreshToken"));
    }

    @Test
    void validateResendVerification_WhenValid_ShouldNotThrowException() {
        ResendVerification data = new ResendVerification(userId, VerificationType.REGISTRATION);

        assertDoesNotThrow(() -> authValidator.validateResendVerification(data));
    }

    @Test
    void validateResendVerification_WhenUserIdIsNull_ShouldThrowException() {
        ResendVerification data = new ResendVerification(null, VerificationType.REGISTRATION);

        ValidationException ex =
            assertThrows(ValidationException.class, () -> authValidator.validateResendVerification(data));

        assertThat(ex.getErrors()).anyMatch(e -> e.field().equals("userId"));
    }

    @Test
    void validateResendVerification_WhenTypeIsNull_ShouldThrowException() {
        ResendVerification data = new ResendVerification(userId, null);

        ValidationException ex =
            assertThrows(ValidationException.class, () -> authValidator.validateResendVerification(data));

        assertThat(ex.getErrors()).anyMatch(e -> e.field().equals("type"));
    }
}