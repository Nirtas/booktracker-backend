package ru.jerael.booktracker.backend.domain.model.book;

public enum BookStatusTransition {
    UPDATE,
    NEW_ATTEMPT,
    INVALID,
    IGNORE
}
