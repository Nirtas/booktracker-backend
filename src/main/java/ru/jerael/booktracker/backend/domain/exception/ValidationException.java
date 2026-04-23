package ru.jerael.booktracker.backend.domain.exception;

import ru.jerael.booktracker.backend.domain.exception.code.CommonErrorCode;
import ru.jerael.booktracker.backend.domain.exception.model.ValidationError;
import java.util.List;

public class ValidationException extends AppException {
    private final List<ValidationError> errors;

    public ValidationException(List<ValidationError> errors) {
        super(CommonErrorCode.VALIDATION_ERROR, "Validation failed");
        this.errors = errors;
    }

    public ValidationException(ValidationError error) {
        this(List.of(error));
    }

    public List<ValidationError> getErrors() {
        return errors;
    }
}
