package ru.jerael.booktracker.backend.data.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.jerael.booktracker.backend.data.db.entity.BookEntity;
import ru.jerael.booktracker.backend.data.db.entity.GenreEntity;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.BookCreation;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookDataMapper {
    private final GenreDataMapper genreDataMapper;
    private final AuthorDataMapper authorDataMapper;
    private final PublisherDataMapper publisherDataMapper;
    private final LanguageDataMapper languageDataMapper;
    private final ReadingAttemptDataMapper readingAttemptDataMapper;
    private final NoteDataMapper noteDataMapper;

    public Book toDomain(BookEntity entity) {
        if (entity == null) return null;

        return new Book(
            entity.getId(),
            entity.getTitle(),
            entity.getAuthor(),
            entity.getCoverFileName(),
            entity.getStatus(),
            entity.getCreatedAt(),
            entity.getGenres() == null ? Collections.emptySet() :
                entity.getGenres().stream().map(genreDataMapper::toDomain).collect(Collectors.toSet()),
            entity.getAuthors() == null ? Collections.emptySet() :
                entity.getAuthors().stream().map(authorDataMapper::toDomain).collect(Collectors.toSet()),
            entity.getDescription(),
            publisherDataMapper.toDomain(entity.getPublisher()),
            languageDataMapper.toDomain(entity.getLanguage()),
            entity.getPublishedOn(),
            entity.getTotalPages(),
            entity.getIsbn10(),
            entity.getIsbn13(),
            entity.getAttempts() == null ? List.of() :
                entity.getAttempts().stream().map(readingAttemptDataMapper::toDomain).toList(),
            entity.getNotes() == null ? List.of() :
                entity.getNotes().stream().map(noteDataMapper::toDomain).toList()
        );
    }

    public BookEntity toEntity(Book book) {
        if (book == null) return null;

        BookEntity entity = new BookEntity();
        entity.setId(book.id());
        entity.setTitle(book.title());
        entity.setAuthor(book.author());
        entity.setCoverFileName(book.coverFileName());
        entity.setStatus(book.status());
        entity.setCreatedAt(book.createdAt());
        entity.setGenres(book.genres() == null ? Collections.emptySet() :
            book.genres().stream().map(genreDataMapper::toEntity).collect(Collectors.toSet()));
        entity.setAuthors(book.authors() == null ? Collections.emptySet() :
            book.authors().stream().map(authorDataMapper::toEntity).collect(Collectors.toSet()));
        entity.setDescription(book.description());
        entity.setPublisher(publisherDataMapper.toEntity(book.publisher()));
        entity.setLanguage(languageDataMapper.toEntity(book.language()));
        entity.setPublishedOn(book.publishedOn());
        entity.setTotalPages(book.totalPages());
        entity.setIsbn10(book.isbn10());
        entity.setIsbn13(book.isbn13());
        entity.setAttempts(book.attempts() == null ? List.of() :
            book.attempts().stream().map(readingAttemptDataMapper::toEntity).toList());
        entity.setNotes(book.notes() == null ? List.of() :
            book.notes().stream().map(noteDataMapper::toEntity).toList());
        return entity;
    }

    public BookEntity toEntity(BookCreation data, Set<GenreEntity> genres) {
        if (data == null) return null;

        BookEntity entity = new BookEntity();
        entity.setTitle(data.title());
        entity.setAuthor(data.author());
        entity.setGenres(genres);
        return entity;
    }
}
