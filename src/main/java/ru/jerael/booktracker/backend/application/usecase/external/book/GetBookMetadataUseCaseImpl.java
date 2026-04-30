package ru.jerael.booktracker.backend.application.usecase.external.book;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import ru.jerael.booktracker.backend.application.annotation.UseCase;
import ru.jerael.booktracker.backend.domain.exception.factory.BookExceptionFactory;
import ru.jerael.booktracker.backend.domain.gateway.book.BookMetadataProvider;
import ru.jerael.booktracker.backend.domain.model.author.Author;
import ru.jerael.booktracker.backend.domain.model.book.BookMetadata;
import ru.jerael.booktracker.backend.domain.model.book.BookSearchQuery;
import ru.jerael.booktracker.backend.domain.model.genre.Genre;
import ru.jerael.booktracker.backend.domain.model.language.Language;
import ru.jerael.booktracker.backend.domain.model.publisher.Publisher;
import ru.jerael.booktracker.backend.domain.repository.AuthorRepository;
import ru.jerael.booktracker.backend.domain.repository.GenreRepository;
import ru.jerael.booktracker.backend.domain.repository.LanguageRepository;
import ru.jerael.booktracker.backend.domain.repository.PublisherRepository;
import ru.jerael.booktracker.backend.domain.usecase.external.book.GetBookMetadataUseCase;
import java.util.Set;
import java.util.stream.Collectors;

@UseCase
@RequiredArgsConstructor
public class GetBookMetadataUseCaseImpl implements GetBookMetadataUseCase {
    private final BookMetadataProvider bookMetadataProvider;
    private final GenreRepository genreRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final LanguageRepository languageRepository;

    @Override
    @Transactional(readOnly = true)
    public BookMetadata execute(BookSearchQuery query) {
        String cleanedIsbn = query.isbn().replaceAll("[^0-9]", "");
        BookSearchQuery cleanedQuery = new BookSearchQuery(cleanedIsbn);

        BookMetadata metadata = bookMetadataProvider.findBook(cleanedQuery)
            .orElseThrow(BookExceptionFactory::bookMetadataNotFound);

        Set<String> genreNames = metadata.genres().stream()
            .map(Genre::name)
            .collect(Collectors.toSet());
        Set<Genre> existingGenres = genreRepository.findAllByNames(genreNames);

        Set<String> authorNames = metadata.authors().stream()
            .map(Author::fullName)
            .collect(Collectors.toSet());
        Set<Author> existingAuthors = authorRepository.findAllByNames(authorNames);
        Set<Author> authors = metadata.authors().stream()
            .map(author -> existingAuthors.stream()
                .filter(existing -> existing.fullName().equalsIgnoreCase(author.fullName()))
                .findFirst()
                .orElse(author))
            .collect(Collectors.toSet());

        Publisher publisher = metadata.publisher() != null
            ? publisherRepository.findByName(metadata.publisher().name()).orElse(metadata.publisher())
            : null;

        Language language = metadata.language() != null
            ? languageRepository.findByCode(metadata.language().code()).orElse(metadata.language())
            : null;

        return new BookMetadata(
            metadata.title(),
            metadata.cover(),
            existingGenres,
            authors,
            metadata.description(),
            publisher,
            language,
            metadata.publishedOn(),
            metadata.totalPages(),
            metadata.isbn10(),
            metadata.isbn13()
        );
    }
}
