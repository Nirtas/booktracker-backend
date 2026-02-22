package ru.jerael.booktracker.backend.domain.exception;

public class ValidationException extends AppException {
    private final String fieldName;

    public ValidationException(String errorCode, String message, String fieldName) {
        super(errorCode, message);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public static ValidationException invalidBookStatus(String status) {
        return new ValidationException(
            "INVALID_BOOK_STATUS",
            "Unknown book status: " + status,
            "status"
        );
    }

    public static ValidationException emptyFileName(String fieldName) {
        return new ValidationException(
            "EMPTY_FILE_NAME",
            "File name cannot be empty",
            fieldName
        );
    }

    public static ValidationException emptyFileContent(String fieldName) {
        return new ValidationException(
            "EMPTY_FILE_CONTENT",
            "File content cannot be empty",
            fieldName
        );
    }

    public static ValidationException unsupportedFileContentType(String contentType) {
        return new ValidationException(
            "UNSUPPORTED_FILE_CONTENT_TYPE",
            "File content type " + contentType + " is not supported",
            "contentType"
        );
    }
}
