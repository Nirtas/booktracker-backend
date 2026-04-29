package ru.jerael.booktracker.backend.domain.usecase.external.book;

import ru.jerael.booktracker.backend.domain.model.book.BookMetadata;
import ru.jerael.booktracker.backend.domain.model.book.BookSearchQuery;

public interface GetBookMetadataUseCase {
    BookMetadata execute(BookSearchQuery query);
}
