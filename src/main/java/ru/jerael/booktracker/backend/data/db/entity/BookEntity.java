package ru.jerael.booktracker.backend.data.db.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.jerael.booktracker.backend.data.db.constant.Tables;
import ru.jerael.booktracker.backend.domain.constant.BookRules;
import ru.jerael.booktracker.backend.domain.model.book.BookStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = Tables.BOOKS)
@Getter
@Setter
@NoArgsConstructor
public class BookEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "title", length = BookRules.TITLE_MAX_LENGTH, nullable = false)
    private String title;

    @Column(name = "author", length = BookRules.AUTHOR_MAX_LENGTH, nullable = false)
    private String author;

    @Column(name = "cover_file_name")
    private String coverFileName;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookStatus status = BookStatus.WANT_TO_READ;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = Tables.BOOK_GENRES,
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<GenreEntity> genres = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = Tables.BOOK_AUTHORS,
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<AuthorEntity> authors = new HashSet<>();

    @Column(name = "description", length = BookRules.DESCRIPTION_MAX_LENGTH)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id")
    private PublisherEntity publisher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_code")
    private LanguageEntity language;

    @Column(name = "published_on")
    private LocalDate publishedOn;

    @Column(name = "total_pages")
    private Integer totalPages;

    @Column(name = "isbn_10", length = 10)
    private String isbn10;

    @Column(name = "isbn_13", length = 13)
    private String isbn13;

    @OneToMany(
        mappedBy = "book",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<ReadingAttemptEntity> attempts = new ArrayList<>();

    @OneToMany(
        mappedBy = "book",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<NoteEntity> notes = new ArrayList<>();
}
