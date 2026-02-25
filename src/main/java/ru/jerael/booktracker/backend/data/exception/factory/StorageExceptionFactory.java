package ru.jerael.booktracker.backend.data.exception.factory;

import ru.jerael.booktracker.backend.data.exception.code.StorageErrorCode;
import ru.jerael.booktracker.backend.domain.exception.InternalException;

public class StorageExceptionFactory {
    public static InternalException error(String details, Throwable cause) {
        return new InternalException(
            StorageErrorCode.ERROR,
            "Error in storage: " + details,
            cause
        );
    }
}
