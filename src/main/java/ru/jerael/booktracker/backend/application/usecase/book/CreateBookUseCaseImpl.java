package ru.jerael.booktracker.backend.application.usecase.book;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jerael.booktracker.backend.domain.exception.factory.GenreExceptionFactory;
import ru.jerael.booktracker.backend.domain.model.Genre;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.BookCreation;
import ru.jerael.booktracker.backend.domain.repository.BookRepository;
import ru.jerael.booktracker.backend.domain.repository.GenreRepository;
import ru.jerael.booktracker.backend.domain.usecase.book.CreateBookUseCase;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreateBookUseCaseImpl implements CreateBookUseCase {
    private final BookRepository bookRepository;
    private final GenreRepository genreRepository;

    @Override
    @Transactional
    public Book execute(BookCreation data, UUID userId) {
        Set<Genre> genres = genreRepository.findAllById(data.genreIds());
        if (genres.size() != data.genreIds().size()) {
            Set<Integer> foundIds = genres.stream().map(Genre::id).collect(Collectors.toSet());
            Set<Integer> missingIds = data.genreIds().stream()
                .filter(id -> !foundIds.contains(id))
                .collect(Collectors.toSet());
            throw GenreExceptionFactory.genresNotFound(missingIds);
        }
        // TODO: provide real data when BookCreation will be updated
        Book newBook = new Book(
            null,
            data.title(),
            null,
            Instant.now(),
            genres,
            Set.of(),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            List.of(),
            List.of()
        );
        return bookRepository.save(newBook, userId);
    }
}
