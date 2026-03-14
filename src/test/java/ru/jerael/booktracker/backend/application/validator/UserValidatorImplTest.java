package ru.jerael.booktracker.backend.application.validator;

import org.junit.jupiter.api.Test;
import ru.jerael.booktracker.backend.domain.exception.ValidationException;
import ru.jerael.booktracker.backend.domain.model.user.UserCreation;
import ru.jerael.booktracker.backend.domain.validator.FieldValidator;
import ru.jerael.booktracker.backend.domain.validator.UserValidator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserValidatorImplTest {
    private final FieldValidator fieldValidator = new FieldValidatorImpl();
    private final UserValidator userValidator = new UserValidatorImpl(fieldValidator);

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
}