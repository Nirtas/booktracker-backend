package ru.jerael.booktracker.backend.domain.validator;

import ru.jerael.booktracker.backend.domain.model.book.BookDetailsUpdate;

public interface BookValidator {
    void validateUpdate(BookDetailsUpdate data);
}
