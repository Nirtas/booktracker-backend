package ru.jerael.booktracker.backend.domain.exception;

import ru.jerael.booktracker.backend.domain.exception.code.ErrorCode;
import java.util.Map;

public class ValidationException extends AppException {
    private final String field;
    private final Map<String, Object> params;

    public ValidationException(ErrorCode errorCode, String message, String field, Map<String, Object> params) {
        super(errorCode, message);
        this.field = field;
        this.params = params;
    }

    public String getField() {
        return field;
    }

    public Map<String, Object> getParams() {
        return params;
    }
}
