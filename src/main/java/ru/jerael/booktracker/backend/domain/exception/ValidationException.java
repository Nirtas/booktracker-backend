package ru.jerael.booktracker.backend.domain.exception;

import ru.jerael.booktracker.backend.domain.exception.code.CommonErrorCode;
import ru.jerael.booktracker.backend.domain.exception.code.ErrorCode;
import ru.jerael.booktracker.backend.domain.exception.model.ValidationError;
import java.util.List;
import java.util.Map;

public class ValidationException extends AppException {
    private final List<ValidationError> errors;

    public ValidationException(List<ValidationError> errors) {
        super(CommonErrorCode.VALIDATION_ERROR, "Validation failed");
        this.errors = errors;
    }

    // TODO: remove this in next versions
    @Deprecated(forRemoval = true)
    public ValidationException(ErrorCode errorCode, String message, String field, Map<String, Object> params) {
        super(errorCode, message);
        this.errors = List.of(new ValidationError(errorCode.name(), field, message, params));
    }

    public List<ValidationError> getErrors() {
        return errors;
    }
}
