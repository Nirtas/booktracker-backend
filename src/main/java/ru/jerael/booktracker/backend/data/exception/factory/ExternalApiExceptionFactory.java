package ru.jerael.booktracker.backend.data.exception.factory;

import ru.jerael.booktracker.backend.data.exception.code.ExternalApiErrorCode;
import ru.jerael.booktracker.backend.domain.exception.InternalException;

public class ExternalApiExceptionFactory {
    public static InternalException error(String details, Throwable cause) {
        return new InternalException(
            ExternalApiErrorCode.ERROR,
            "API error: " + details,
            cause
        );
    }
}
