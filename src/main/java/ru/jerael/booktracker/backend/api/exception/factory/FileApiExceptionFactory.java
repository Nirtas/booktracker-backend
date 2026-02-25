package ru.jerael.booktracker.backend.api.exception.factory;

import ru.jerael.booktracker.backend.api.exception.code.ApiErrorCode;
import ru.jerael.booktracker.backend.domain.exception.InternalException;

public class FileApiExceptionFactory {
    public static InternalException readError(String fileName, Throwable cause) {
        return new InternalException(
            ApiErrorCode.INTERNAL_SERVER_ERROR,
            "Failed to read file from request: " + fileName,
            cause
        );
    }
}
