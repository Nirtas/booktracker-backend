package ru.jerael.booktracker.backend.domain.usecase.book;

import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.UploadCoverPayload;

public interface UploadCoverUseCase {
    Book execute(UploadCoverPayload data);
}
