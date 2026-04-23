package ru.jerael.booktracker.backend.data.mapper;

import lombok.RequiredArgsConstructor;
import ru.jerael.booktracker.backend.data.annotation.DataMapper;
import ru.jerael.booktracker.backend.data.db.entity.BookEntity;
import ru.jerael.booktracker.backend.data.db.entity.NoteEntity;
import ru.jerael.booktracker.backend.data.db.entity.ReadingAttemptEntity;
import ru.jerael.booktracker.backend.data.db.entity.UserEntity;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@DataMapper
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
            entity.getUser().getId(),
            entity.getTitle(),
            entity.getCoverFileName(),
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

        UserEntity userEntity = new UserEntity();
        userEntity.setId(book.userId());
        entity.setUser(userEntity);

        entity.setTitle(book.title());
        entity.setCoverFileName(book.coverFileName());
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

        entity.setAttempts(
            book.attempts() == null ? List.of() :
                book.attempts().stream()
                .map(readingAttempt -> {
                    ReadingAttemptEntity attemptEntity = readingAttemptDataMapper.toEntity(readingAttempt);
                    attemptEntity.setBook(entity);
                    return attemptEntity;
                })
                .toList()
        );

        entity.setNotes(
            book.notes() == null ? List.of() :
                book.notes().stream()
                .map(note -> {
                    NoteEntity noteEntity = noteDataMapper.toEntity(note);
                    noteEntity.setBook(entity);
                    return noteEntity;
                })
                .toList()
        );

        return entity;
    }
}
