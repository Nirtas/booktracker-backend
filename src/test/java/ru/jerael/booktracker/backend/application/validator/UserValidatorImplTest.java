package ru.jerael.booktracker.backend.application.validator;

import org.junit.jupiter.api.Test;
import ru.jerael.booktracker.backend.domain.constant.UserRules;
import ru.jerael.booktracker.backend.domain.exception.ValidationException;
import ru.jerael.booktracker.backend.domain.exception.code.CommonValidationErrorCode;
import ru.jerael.booktracker.backend.domain.exception.code.EmailErrorCode;
import ru.jerael.booktracker.backend.domain.exception.code.PasswordErrorCode;
import ru.jerael.booktracker.backend.domain.model.user.UserCreation;
import ru.jerael.booktracker.backend.domain.validator.UserValidator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserValidatorImplTest {
    private final UserValidator userValidator = new UserValidatorImpl();

    @Test
    void validateCreation_WhenValidDataProvided_ShouldPassValidationWithoutErrors() {
        UserCreation data = new UserCreation("test@example.com", "Password123!");

        assertDoesNotThrow(() -> userValidator.validateCreation(data));
    }

    @Test
    void validateCreation_WhenMultipleInvalidFields_ShouldThrowValidationExceptionWithAllErrors() {
        UserCreation data = new UserCreation("invalid email", "aaa");

        ValidationException exception =
            assertThrows(ValidationException.class, () -> userValidator.validateCreation(data));

        assertThat(exception.getErrors()).hasSizeGreaterThan(1);
        assertThat(exception.getErrors()).anyMatch(e -> e.field().equals("email"));
        assertThat(exception.getErrors()).anyMatch(e -> e.field().equals("password"));
    }

    @Test
    void validateEmail_WhenEmailIsEmpty_ShouldThrowValidationException() {
        UserCreation data = new UserCreation("", "Password123!");

        ValidationException exception =
            assertThrows(ValidationException.class, () -> userValidator.validateCreation(data));

        assertThat(exception.getErrors())
            .extracting("code")
            .contains(CommonValidationErrorCode.FIELD_CANNOT_BE_EMPTY.name());
    }

    @Test
    void validateEmail_WhenEmailTooLong_ShouldThrowValidationException() {
        UserCreation data = new UserCreation("a".repeat(UserRules.EMAIL_MAX_LENGTH + 1), "Password123!");

        ValidationException exception =
            assertThrows(ValidationException.class, () -> userValidator.validateCreation(data));

        assertThat(exception.getErrors())
            .extracting("code")
            .contains(CommonValidationErrorCode.FIELD_TOO_LONG.name());

        assertThat(exception.getErrors().get(0).params().get("max"));
    }

    @Test
    void validateEmail_WhenFormatIsInvalid_ShouldThrowValidationException() {
        UserCreation data = new UserCreation("invalid email", "Password123!");

        ValidationException exception =
            assertThrows(ValidationException.class, () -> userValidator.validateCreation(data));

        assertThat(exception.getErrors())
            .extracting("code")
            .contains(EmailErrorCode.INVALID_FORMAT.name());
    }

    @Test
    void validatePassword_WhenPasswordIsEmpty_ShouldThrowValidationException() {
        UserCreation data = new UserCreation("test@example.com", "");

        ValidationException exception =
            assertThrows(ValidationException.class, () -> userValidator.validateCreation(data));

        assertThat(exception.getErrors())
            .extracting("code")
            .contains(CommonValidationErrorCode.FIELD_CANNOT_BE_EMPTY.name());
    }

    @Test
    void validatePassword_WhenPasswordTooShort_ShouldThrowValidationException() {
        UserCreation data = new UserCreation("test@example.com", "aaa");

        ValidationException exception =
            assertThrows(ValidationException.class, () -> userValidator.validateCreation(data));

        assertThat(exception.getErrors())
            .extracting("code")
            .contains(CommonValidationErrorCode.FIELD_TOO_SHORT.name());

        assertThat(exception.getErrors().get(0).params().get("max"));
    }

    @Test
    void validatePassword_WhenLowercaseCharDoesNotExists_ShouldThrowValidationException() {
        UserCreation data = new UserCreation("test@example.com", "PASSWORD123!");

        ValidationException exception =
            assertThrows(ValidationException.class, () -> userValidator.validateCreation(data));

        assertThat(exception.getErrors())
            .extracting("code")
            .contains(PasswordErrorCode.NEEDS_LOWERCASE.name());
    }

    @Test
    void validatePassword_WhenUppercaseCharDoesNotExists_ShouldThrowValidationException() {
        UserCreation data = new UserCreation("test@example.com", "password123!");

        ValidationException exception =
            assertThrows(ValidationException.class, () -> userValidator.validateCreation(data));

        assertThat(exception.getErrors())
            .extracting("code")
            .contains(PasswordErrorCode.NEEDS_UPPERCASE.name());
    }

    @Test
    void validatePassword_WhenDigitDoesNotExists_ShouldThrowValidationException() {
        UserCreation data = new UserCreation("test@example.com", "Password!");

        ValidationException exception =
            assertThrows(ValidationException.class, () -> userValidator.validateCreation(data));

        assertThat(exception.getErrors())
            .extracting("code")
            .contains(PasswordErrorCode.NEEDS_DIGIT.name());
    }

    @Test
    void validatePassword_WhenSpecialCharDoesNotExists_ShouldThrowValidationException() {
        UserCreation data = new UserCreation("test@example.com", "Password123");

        ValidationException exception =
            assertThrows(ValidationException.class, () -> userValidator.validateCreation(data));

        assertThat(exception.getErrors())
            .extracting("code")
            .contains(PasswordErrorCode.NEEDS_SPECIAL_CHAR.name());
    }

    @Test
    void validatePassword_WhenForbiddenCharExists_ShouldThrowValidationException() {
        UserCreation data = new UserCreation("test@example.com", "Password 123!");

        ValidationException exception =
            assertThrows(ValidationException.class, () -> userValidator.validateCreation(data));

        assertThat(exception.getErrors())
            .extracting("code")
            .contains(PasswordErrorCode.FORBIDDEN_CHAR.name());

        assertNotNull(exception.getErrors().get(0).params().get("forbidden"));
    }
}