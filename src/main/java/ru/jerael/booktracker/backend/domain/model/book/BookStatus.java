package ru.jerael.booktracker.backend.domain.model.book;

import ru.jerael.booktracker.backend.domain.exception.factory.BookExceptionFactory;

public enum BookStatus {
    WANT_TO_READ("want_to_read"),
    READING("reading"),
    COMPLETED("completed");

    private final String value;

    BookStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static BookStatus fromString(String value) {
        if (value == null) return null;
        for (BookStatus status : BookStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw BookExceptionFactory.invalidStatus(value);
    }

    public static BookStatus defaultStatus() {
        return BookStatus.WANT_TO_READ;
    }
}
