package ru.jerael.booktracker.backend.application.usecase.book;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jerael.booktracker.backend.domain.exception.NotFoundException;
import ru.jerael.booktracker.backend.domain.model.Genre;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.BookCreation;
import ru.jerael.booktracker.backend.domain.repository.BookRepository;
import ru.jerael.booktracker.backend.domain.repository.GenreRepository;
import ru.jerael.booktracker.backend.domain.usecase.book.CreateBookUseCase;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreateBookUseCaseImpl implements CreateBookUseCase {
    private final BookRepository bookRepository;
    private final GenreRepository genreRepository;

    @Override
    @Transactional
    public Book execute(BookCreation data) {
        // TODO: add genreRepository.findAllById() and replace this
        Set<Genre> genres = data.genreIds().stream()
            .map(id ->
                genreRepository.getGenreById(id).orElseThrow(() -> NotFoundException.genreNotFound(id))
            ).collect(Collectors.toSet());
        return bookRepository.create(data, genres);
    }
}
