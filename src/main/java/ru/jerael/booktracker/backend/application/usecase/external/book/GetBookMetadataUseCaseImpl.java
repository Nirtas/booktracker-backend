package ru.jerael.booktracker.backend.application.usecase.external.book;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import ru.jerael.booktracker.backend.application.annotation.UseCase;
import ru.jerael.booktracker.backend.domain.exception.factory.BookExceptionFactory;
import ru.jerael.booktracker.backend.domain.gateway.book.BookMetadataProvider;
import ru.jerael.booktracker.backend.domain.model.book.BookMetadata;
import ru.jerael.booktracker.backend.domain.model.book.BookSearchQuery;
import ru.jerael.booktracker.backend.domain.usecase.external.book.GetBookMetadataUseCase;

@UseCase
@RequiredArgsConstructor
public class GetBookMetadataUseCaseImpl implements GetBookMetadataUseCase {
    private final BookMetadataProvider bookMetadataProvider;

    @Override
    @Transactional(readOnly = true)
    public BookMetadata execute(BookSearchQuery query) {
        return bookMetadataProvider.findBook(query).orElseThrow(BookExceptionFactory::bookMetadataNotFound);
    }
}
