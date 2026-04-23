package ru.jerael.booktracker.backend.domain.exception.factory;

import ru.jerael.booktracker.backend.domain.exception.code.BookErrorCode;
import ru.jerael.booktracker.backend.domain.exception.model.ValidationError;
import java.util.Map;

public class BookErrorFactory {
    public static ValidationError invalidTotalPages() {
        return new ValidationError(
            BookErrorCode.INVALID_TOTAL_PAGES.name(),
            "totalPages",
            "Total pages must be more than zero",
            Map.of("min", 1)
        );
    }
}
