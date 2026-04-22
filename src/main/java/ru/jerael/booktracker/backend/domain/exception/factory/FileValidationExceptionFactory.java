package ru.jerael.booktracker.backend.domain.exception.factory;

import ru.jerael.booktracker.backend.domain.constant.ImageRules;
import ru.jerael.booktracker.backend.domain.exception.ValidationException;
import ru.jerael.booktracker.backend.domain.exception.code.FileValidationErrorCode;
import ru.jerael.booktracker.backend.domain.exception.model.ValidationError;
import java.util.Map;

public class FileValidationExceptionFactory {
    public static ValidationException emptyFileName(String field) {
        return new ValidationException(
            new ValidationError(
                FileValidationErrorCode.EMPTY_FILE_NAME.name(),
                field,
                "File name cannot be empty",
                Map.of()
            )
        );
    }

    public static ValidationException emptyFileContent(String field) {
        return new ValidationException(
            new ValidationError(
                FileValidationErrorCode.EMPTY_FILE_CONTENT.name(),
                field,
                "File content cannot be empty",
                Map.of()
            )
        );
    }

    public static ValidationException unsupportedFileContentType(String contentType, String field) {
        return new ValidationException(
            new ValidationError(
                FileValidationErrorCode.UNSUPPORTED_FILE_CONTENT_TYPE.name(),
                field,
                "File content type " + contentType + " is not supported",
                Map.of("allowed", ImageRules.ALLOWED_MIME_TYPES)
            )
        );
    }
}
