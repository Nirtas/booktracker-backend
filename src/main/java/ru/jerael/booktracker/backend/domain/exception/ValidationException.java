package ru.jerael.booktracker.backend.domain.exception;

public class ValidationException extends AppException {
    private final String fieldName;

    public ValidationException(String message, String errorCode, String fieldName) {
        super(message, errorCode);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public static ValidationException invalidBookStatus(String status) {
        return new ValidationException(
            "Unknown book status: " + status,
            "INVALID_BOOK_STATUS",
            "status"
        );
    }

    public static ValidationException fieldCannotBeEmpty(String fieldName) {
        return new ValidationException(
            fieldName + " cannot be empty",
            "FIELD_CANNOT_BE_EMPTY",
            fieldName
        );
    }

    public static ValidationException fieldTooLong(String fieldName, int maxLength) {
        return new ValidationException(
            fieldName + " is too long (max " + maxLength + ")",
            "FIELD_TOO_LONG",
            fieldName
        );
    }

    public static ValidationException emptyFileName(String fieldName) {
        return new ValidationException(
            "File name cannot be empty",
            "EMPTY_FILE_NAME",
            fieldName
        );
    }

    public static ValidationException emptyFileContent(String fieldName) {
        return new ValidationException(
            "File content cannot be empty",
            "EMPTY_FILE_CONTENT",
            fieldName
        );
    }

    public static ValidationException unsupportedFileContentType(String contentType) {
        return new ValidationException(
            "File content type " + contentType + " is not supported",
            "UNSUPPORTED_FILE_CONTENT_TYPE",
            "contentType"
        );
    }
}
