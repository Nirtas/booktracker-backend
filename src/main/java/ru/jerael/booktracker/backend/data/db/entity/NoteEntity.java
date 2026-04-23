package ru.jerael.booktracker.backend.data.db.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.jerael.booktracker.backend.data.db.constant.Tables;
import ru.jerael.booktracker.backend.domain.constant.NoteRules;
import ru.jerael.booktracker.backend.domain.model.note.NoteType;
import java.time.Instant;
import java.util.UUID;

@Table(name = Tables.NOTES)
@Getter
@Setter
@Entity
@NoArgsConstructor
public class NoteEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private BookEntity book;

    @Column(name = "type", length = NoteRules.NOTE_TYPE_MAX_LENGTH, nullable = false)
    @Enumerated(EnumType.STRING)
    private NoteType type;

    @Column(name = "text_content", length = NoteRules.NOTE_TEXT_CONTENT_MAX_LENGTH)
    private String textContent;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "page_number", nullable = false)
    private int pageNumber;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
