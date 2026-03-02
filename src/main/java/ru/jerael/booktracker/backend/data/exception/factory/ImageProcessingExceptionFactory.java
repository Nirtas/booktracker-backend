package ru.jerael.booktracker.backend.data.exception.factory;

import ru.jerael.booktracker.backend.data.exception.code.ImageProcessingErrorCode;
import ru.jerael.booktracker.backend.domain.exception.InternalException;

public class ImageProcessingExceptionFactory {
    public static InternalException failedToProcess(String details, Throwable cause) {
        return new InternalException(
            ImageProcessingErrorCode.PROCESSING_ERROR,
            "Image processing error: " + details,
            cause
        );
    }
}
