package ru.jerael.booktracker.backend.domain.gateway.book;

import ru.jerael.booktracker.backend.domain.model.book.BookMetadata;
import ru.jerael.booktracker.backend.domain.model.book.BookSearchQuery;
import java.util.Optional;

public interface BookMetadataProvider {
    Optional<BookMetadata> findBook(BookSearchQuery query);
}
