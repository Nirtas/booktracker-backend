package ru.jerael.booktracker.backend.application.usecase.book;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jerael.booktracker.backend.domain.exception.factory.BookExceptionFactory;
import ru.jerael.booktracker.backend.domain.exception.factory.GenreExceptionFactory;
import ru.jerael.booktracker.backend.domain.exception.factory.LanguageExceptionFactory;
import ru.jerael.booktracker.backend.domain.model.Genre;
import ru.jerael.booktracker.backend.domain.model.author.Author;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.BookDetailsUpdate;
import ru.jerael.booktracker.backend.domain.model.book.BookStatus;
import ru.jerael.booktracker.backend.domain.model.language.Language;
import ru.jerael.booktracker.backend.domain.model.publisher.Publisher;
import ru.jerael.booktracker.backend.domain.model.reading_attempt.ReadingAttempt;
import ru.jerael.booktracker.backend.domain.model.reading_session.ReadingSession;
import ru.jerael.booktracker.backend.domain.repository.*;
import ru.jerael.booktracker.backend.domain.usecase.book.UpdateBookDetailsUseCase;
import ru.jerael.booktracker.backend.domain.validator.BookValidator;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UpdateBookDetailsUseCaseImpl implements UpdateBookDetailsUseCase {
    private final BookRepository bookRepository;
    private final GenreRepository genreRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final LanguageRepository languageRepository;
    private final BookValidator bookValidator;

    @Override
    @Transactional
    public Book execute(BookDetailsUpdate data) {
        bookValidator.validateUpdate(data);

        Book book = bookRepository.findByIdAndUserId(data.bookId(), data.userId())
            .orElseThrow(() -> BookExceptionFactory.bookNotFound(data.bookId()));

        Set<Genre> updatedGenres = book.genres();
        if (data.genreIds() != null) {
            updatedGenres = genreRepository.findAllById(data.genreIds());
            if (updatedGenres.size() != data.genreIds().size()) {
                Set<Integer> foundIds = updatedGenres.stream().map(Genre::id).collect(Collectors.toSet());
                Set<Integer> missingIds = data.genreIds().stream()
                    .filter(genreId -> !foundIds.contains(genreId))
                    .collect(Collectors.toSet());
                throw GenreExceptionFactory.genresNotFound(missingIds);
            }
        }

        Set<Author> authors = book.authors();
        if (data.authorNames() != null) {
            authors = data.authorNames().stream()
                .map(name -> authorRepository.findByFullName(name)
                    .orElseGet(() -> authorRepository.save(new Author(null, name)))
                )
                .collect(Collectors.toSet());
        }

        Publisher publisher = book.publisher();
        if (data.publisherName() != null) {
            String publisherName = data.publisherName();
            publisher = publisherRepository.findByName(publisherName)
                .orElseGet(() -> publisherRepository.save(new Publisher(null, publisherName)));
        }

        Language language = book.language();
        if (data.languageCode() != null) {
            String languageCode = data.languageCode();
            language = languageRepository.findByCode(languageCode)
                .orElseThrow(() -> LanguageExceptionFactory.languageNotFound(languageCode));
        }

        List<ReadingAttempt> updatedAttempts = book.attempts();
        if (data.status() != null) {
            updatedAttempts = changeStatus(book, data.status());
        }

        Book updatedBook = new Book(
            book.id(),
            book.userId(),
            data.title() != null ? data.title().trim() : book.title(),
            book.coverFileName(),
            book.createdAt(),
            updatedGenres,
            authors,
            data.description() != null ? data.description().trim() : book.description(),
            publisher,
            language,
            data.publishedOn() != null ? data.publishedOn() : book.publishedOn(),
            data.totalPages() != null ? data.totalPages() : book.totalPages(),
            data.isbn10() != null ? data.isbn10() : book.isbn10(),
            data.isbn13() != null ? data.isbn13() : book.isbn13(),
            updatedAttempts,
            book.notes()
        );
        return bookRepository.save(updatedBook, data.userId());
    }

    private List<ReadingAttempt> changeStatus(Book book, BookStatus newStatus) {
        List<ReadingAttempt> attempts = new ArrayList<>(book.attempts());
        if (attempts.isEmpty()) {
            throw BookExceptionFactory.readingAttemptsNotFound(book.id());
        }

        ReadingAttempt lastAttempt = attempts.get(attempts.size() - 1);
        if (lastAttempt.status() == newStatus) return attempts;

        if (lastAttempt.status() == BookStatus.READ) {
            ReadingAttempt newAttempt = new ReadingAttempt(
                null, book.id(), newStatus, Instant.now(), null, List.of()
            );
            attempts.add(newAttempt);
        } else {
            ReadingAttempt updatedAttempt = lastAttempt.withStatus(newStatus);
            if (newStatus == BookStatus.READ) {
                updatedAttempt = updatedAttempt.withFinishedAt(Instant.now());
                if (updatedAttempt.sessions().isEmpty() && book.totalPages() != null) {
                    ReadingSession finalSession = new ReadingSession(
                        null, null, 0, book.totalPages(), Instant.now(), Instant.now()
                    );
                    updatedAttempt = updatedAttempt.withSessions(List.of(finalSession));
                }
            }
            attempts.set(attempts.size() - 1, updatedAttempt);
        }

        return attempts;
    }
}
