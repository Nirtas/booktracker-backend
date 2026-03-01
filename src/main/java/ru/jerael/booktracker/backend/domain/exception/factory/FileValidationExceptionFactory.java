package ru.jerael.booktracker.backend.domain.exception.factory;

import ru.jerael.booktracker.backend.domain.constant.ImageRules;
import ru.jerael.booktracker.backend.domain.exception.ValidationException;
import ru.jerael.booktracker.backend.domain.exception.code.FileValidationErrorCode;
import java.util.Map;

public class FileValidationExceptionFactory {
    public static ValidationException emptyFileName(String field) {
        return new ValidationException(
            FileValidationErrorCode.EMPTY_FILE_NAME,
            "File name cannot be empty",
            field,
            Map.of()
        );
    }

    public static ValidationException emptyFileContent(String field) {
        return new ValidationException(
            FileValidationErrorCode.EMPTY_FILE_CONTENT,
            "File content cannot be empty",
            field,
            Map.of()
        );
    }

    public static ValidationException unsupportedFileContentType(String contentType, String field) {
        return new ValidationException(
            FileValidationErrorCode.UNSUPPORTED_FILE_CONTENT_TYPE,
            "File content type " + contentType + " is not supported",
            field,
            Map.of("allowed", ImageRules.ALLOWED_MIME_TYPES)
        );
    }
}
