package ru.jerael.booktracker.backend.domain.usecase.book;

import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.UploadCover;
import java.util.UUID;

public interface UploadCoverUseCase {
    Book execute(UUID bookId, UploadCover data);
}
