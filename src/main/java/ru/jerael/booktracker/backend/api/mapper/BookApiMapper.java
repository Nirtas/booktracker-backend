package ru.jerael.booktracker.backend.api.mapper;

import org.springframework.stereotype.Component;
import ru.jerael.booktracker.backend.api.dto.book.BookCreationRequest;
import ru.jerael.booktracker.backend.api.dto.book.BookResponse;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.BookCreation;
import java.util.List;

@Component
public class BookApiMapper {
    private final GenreApiMapper genreApiMapper;

    public BookApiMapper(GenreApiMapper genreApiMapper) {
        this.genreApiMapper = genreApiMapper;
    }

    public BookResponse toResponse(Book book) {
        return new BookResponse(
            book.id(),
            book.title(),
            book.author(),
            book.coverUrl(),
            book.status().getValue(),
            book.createdAt().toEpochMilli(),
            genreApiMapper.toResponses(book.genres())
        );
    }

    public List<BookResponse> toResponses(List<Book> books) {
        return books.stream().map(this::toResponse).toList();
    }

    public BookCreation toDomain(BookCreationRequest request) {
        return new BookCreation(
            request.title().trim(),
            request.author().trim(),
            request.genreIds()
        );
    }
}
