package ru.jerael.booktracker.backend.application.validator;

import org.junit.jupiter.api.Test;
import ru.jerael.booktracker.backend.domain.constant.UserRules;
import ru.jerael.booktracker.backend.domain.exception.code.CommonValidationErrorCode;
import ru.jerael.booktracker.backend.domain.exception.code.EmailErrorCode;
import ru.jerael.booktracker.backend.domain.exception.code.PasswordErrorCode;
import ru.jerael.booktracker.backend.domain.exception.model.ValidationError;
import ru.jerael.booktracker.backend.domain.validator.FieldValidator;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FieldValidatorImplTest {
    private final FieldValidator fieldValidator = new FieldValidatorImpl();

    @Test
    void validateEmail_WhenValid_ShouldReturnEmptyList() {
        String email = "test@example.com";

        List<ValidationError> errors = fieldValidator.validateEmail(email);

        assertThat(errors).isEmpty();
    }

    @Test
    void validateEmail_WhenEmailIsEmpty_ShouldReturnEmptyFieldError() {
        String email = "";

        List<ValidationError> errors = fieldValidator.validateEmail(email);

        assertThat(errors)
            .extracting("code")
            .contains(CommonValidationErrorCode.FIELD_CANNOT_BE_EMPTY.name());
    }

    @Test
    void validateEmail_WhenEmailTooLong_ShouldReturnTooLongError() {
        String email = "a".repeat(UserRules.EMAIL_MAX_LENGTH + 1);

        List<ValidationError> errors = fieldValidator.validateEmail(email);

        assertThat(errors)
            .extracting("code")
            .contains(CommonValidationErrorCode.FIELD_TOO_LONG.name());

        assertThat(errors.get(0).params().get("max"));
    }

    @Test
    void validateEmail_WhenFormatIsInvalid_ShouldReturnInvalidFormatError() {
        String email = "invalid email";

        List<ValidationError> errors = fieldValidator.validateEmail(email);

        assertThat(errors)
            .extracting("code")
            .contains(EmailErrorCode.INVALID_FORMAT.name());
    }

    @Test
    void validatePassword_WhenValid_ShouldReturnEmptyList() {
        String password = "Password123!";

        List<ValidationError> errors = fieldValidator.validatePassword(password);

        assertThat(errors).isEmpty();
    }

    @Test
    void validatePassword_WhenPasswordIsEmpty_ShouldReturnEmptyFieldError() {
        String password = "";

        List<ValidationError> errors = fieldValidator.validatePassword(password);

        assertThat(errors)
            .extracting("code")
            .contains(CommonValidationErrorCode.FIELD_CANNOT_BE_EMPTY.name());
    }

    @Test
    void validatePassword_WhenPasswordTooShort_ShouldReturnTooShortError() {
        String password = "aaa";

        List<ValidationError> errors = fieldValidator.validatePassword(password);

        assertThat(errors)
            .extracting("code")
            .contains(CommonValidationErrorCode.FIELD_TOO_SHORT.name());

        assertThat(errors.get(0).params().get("max"));
    }

    @Test
    void validatePassword_WhenLowercaseCharDoesNotExists_ShouldReturnNeedsLowercaseError() {
        String password = "PASSWORD123!";

        List<ValidationError> errors = fieldValidator.validatePassword(password);

        assertThat(errors)
            .extracting("code")
            .contains(PasswordErrorCode.NEEDS_LOWERCASE.name());
    }

    @Test
    void validatePassword_WhenUppercaseCharDoesNotExists_ShouldReturnNeedsUppercaseError() {
        String password = "password123!";

        List<ValidationError> errors = fieldValidator.validatePassword(password);

        assertThat(errors)
            .extracting("code")
            .contains(PasswordErrorCode.NEEDS_UPPERCASE.name());
    }

    @Test
    void validatePassword_WhenDigitDoesNotExists_ShouldReturnNeedsDigitError() {
        String password = "Password!";

        List<ValidationError> errors = fieldValidator.validatePassword(password);

        assertThat(errors)
            .extracting("code")
            .contains(PasswordErrorCode.NEEDS_DIGIT.name());
    }

    @Test
    void validatePassword_WhenSpecialCharDoesNotExists_ShouldReturnNeedsSpecialCharError() {
        String password = "Password123";

        List<ValidationError> errors = fieldValidator.validatePassword(password);

        assertThat(errors)
            .extracting("code")
            .contains(PasswordErrorCode.NEEDS_SPECIAL_CHAR.name());
    }

    @Test
    void validatePassword_WhenForbiddenCharExists_ShouldReturnForbiddenCharError() {
        String password = "Password 123!";

        List<ValidationError> errors = fieldValidator.validatePassword(password);

        assertThat(errors)
            .extracting("code")
            .contains(PasswordErrorCode.FORBIDDEN_CHAR.name());

        assertNotNull(errors.get(0).params().get("forbidden"));
    }
}