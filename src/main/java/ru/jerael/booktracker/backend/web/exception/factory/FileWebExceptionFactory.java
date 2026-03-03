package ru.jerael.booktracker.backend.web.exception.factory;

import ru.jerael.booktracker.backend.web.exception.code.WebErrorCode;
import ru.jerael.booktracker.backend.domain.exception.InternalException;

public class FileWebExceptionFactory {
    public static InternalException readError(String fileName, Throwable cause) {
        return new InternalException(
            WebErrorCode.INTERNAL_SERVER_ERROR,
            "Failed to read file from request: " + fileName,
            cause
        );
    }
}
