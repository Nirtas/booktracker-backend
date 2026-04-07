package ru.jerael.booktracker.backend.application.usecase.book;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jerael.booktracker.backend.domain.exception.factory.GenreExceptionFactory;
import ru.jerael.booktracker.backend.domain.exception.factory.LanguageExceptionFactory;
import ru.jerael.booktracker.backend.domain.model.Genre;
import ru.jerael.booktracker.backend.domain.model.author.Author;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.BookCreation;
import ru.jerael.booktracker.backend.domain.model.book.BookStatus;
import ru.jerael.booktracker.backend.domain.model.language.Language;
import ru.jerael.booktracker.backend.domain.model.publisher.Publisher;
import ru.jerael.booktracker.backend.domain.model.reading_attempt.ReadingAttempt;
import ru.jerael.booktracker.backend.domain.model.reading_session.ReadingSession;
import ru.jerael.booktracker.backend.domain.repository.*;
import ru.jerael.booktracker.backend.domain.usecase.book.CreateBookUseCase;
import ru.jerael.booktracker.backend.domain.validator.BookValidator;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreateBookUseCaseImpl implements CreateBookUseCase {
    private final BookRepository bookRepository;
    private final GenreRepository genreRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final LanguageRepository languageRepository;
    private final BookValidator bookValidator;

    @Override
    @Transactional
    public Book execute(BookCreation data) {
        bookValidator.validateCreation(data);

        Set<Genre> genres = genreRepository.findAllById(data.genreIds());
        if (genres.size() != data.genreIds().size()) {
            Set<Integer> foundIds = genres.stream().map(Genre::id).collect(Collectors.toSet());
            Set<Integer> missingIds = data.genreIds().stream()
                .filter(id -> !foundIds.contains(id))
                .collect(Collectors.toSet());
            throw GenreExceptionFactory.genresNotFound(missingIds);
        }

        Set<Author> authors = new HashSet<>();
        if (data.authorNames() != null && !data.authorNames().isEmpty()) {
            authors = data.authorNames().stream()
                .map(name -> authorRepository.findByFullName(name)
                    .orElseGet(() -> authorRepository.save(new Author(null, name)))
                )
                .collect(Collectors.toSet());
        }

        Publisher publisher = null;
        if (data.publisherName() != null && !data.publisherName().isBlank()) {
            String publisherName = data.publisherName();
            publisher = publisherRepository.findByName(publisherName)
                .orElseGet(() -> publisherRepository.save(new Publisher(null, publisherName)));
        }

        Language language = null;
        if (data.languageCode() != null && !data.languageCode().isBlank()) {
            String languageCode = data.languageCode();
            language = languageRepository.findByCode(languageCode)
                .orElseThrow(() -> LanguageExceptionFactory.languageNotFound(languageCode));
        }

        List<ReadingSession> initialSessions = List.of();
        if (data.status() == BookStatus.COMPLETED && data.totalPages() != null) {
            initialSessions = List.of(
                new ReadingSession(null,
                    null,
                    0,
                    data.totalPages(),
                    Instant.now(),
                    Instant.now()
                )
            );
        }

        ReadingAttempt initialAttempt = new ReadingAttempt(
            null,
            null,
            data.status(),
            Instant.now(),
            data.status() == BookStatus.COMPLETED ? Instant.now() : null,
            initialSessions
        );

        Book newBook = new Book(
            null,
            data.userId(),
            data.title(),
            null,
            Instant.now(),
            genres,
            authors,
            data.description(),
            publisher,
            language,
            data.publishedOn(),
            data.totalPages(),
            data.isbn10(),
            data.isbn13(),
            List.of(initialAttempt),
            List.of()
        );
        return bookRepository.save(newBook, data.userId());
    }
}
