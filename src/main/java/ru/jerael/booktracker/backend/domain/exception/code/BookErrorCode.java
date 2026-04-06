package ru.jerael.booktracker.backend.domain.exception.code;

public enum BookErrorCode implements ErrorCode {
    BOOK_NOT_FOUND,
    INVALID_BOOK_STATUS,
    COVER_NOT_FOUND,
    INVALID_TOTAL_PAGES,
    READING_ATTEMPTS_NOT_FOUND
}
