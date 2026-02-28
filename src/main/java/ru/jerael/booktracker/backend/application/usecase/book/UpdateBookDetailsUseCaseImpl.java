package ru.jerael.booktracker.backend.application.usecase.book;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jerael.booktracker.backend.domain.exception.factory.BookExceptionFactory;
import ru.jerael.booktracker.backend.domain.exception.factory.GenreExceptionFactory;
import ru.jerael.booktracker.backend.domain.model.Genre;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.BookDetailsUpdate;
import ru.jerael.booktracker.backend.domain.repository.BookRepository;
import ru.jerael.booktracker.backend.domain.repository.GenreRepository;
import ru.jerael.booktracker.backend.domain.usecase.book.UpdateBookDetailsUseCase;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UpdateBookDetailsUseCaseImpl implements UpdateBookDetailsUseCase {
    private final BookRepository bookRepository;
    private final GenreRepository genreRepository;

    @Override
    @Transactional
    public Book execute(UUID id, BookDetailsUpdate data) {
        Book book = bookRepository.findById(id).orElseThrow(() -> BookExceptionFactory.notFound(id));

        Set<Genre> updatedGenres = book.genres();
        if (data.genreIds() != null) {
            updatedGenres = genreRepository.findAllById(data.genreIds());
            if (updatedGenres.size() != data.genreIds().size()) {
                Set<Integer> foundIds = updatedGenres.stream().map(Genre::id).collect(Collectors.toSet());
                Set<Integer> missingIds = data.genreIds().stream()
                    .filter(genreId -> !foundIds.contains(genreId))
                    .collect(Collectors.toSet());
                throw GenreExceptionFactory.notFound(missingIds);
            }
        }

        Book updatedBook = new Book(
            book.id(),
            data.title() != null ? data.title().trim() : book.title(),
            data.author() != null ? data.author().trim() : book.author(),
            book.coverFileName(),
            data.status() != null ? data.status() : book.status(),
            book.createdAt(),
            updatedGenres
        );
        return bookRepository.save(updatedBook);
    }
}
