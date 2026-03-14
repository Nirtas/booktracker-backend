package ru.jerael.booktracker.backend.domain.exception.factory;

import ru.jerael.booktracker.backend.domain.exception.code.EmailErrorCode;
import ru.jerael.booktracker.backend.domain.exception.model.ValidationError;
import java.util.Map;

public class EmailValidationErrorFactory {
    public static ValidationError invalidFormat() {
        return new ValidationError(
            EmailErrorCode.INVALID_FORMAT.name(),
            "email",
            "Invalid email format",
            Map.of()
        );
    }
}
