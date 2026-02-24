package ru.jerael.booktracker.backend.domain.exception.code;

public enum FileValidationErrorCode implements ErrorCode {
    EMPTY_FILE_NAME,
    EMPTY_FILE_CONTENT,
    UNSUPPORTED_FILE_CONTENT_TYPE
}
