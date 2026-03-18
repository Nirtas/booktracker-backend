package ru.jerael.booktracker.backend.data.db.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.jerael.booktracker.backend.data.db.constant.Tables;
import ru.jerael.booktracker.backend.domain.constant.AuthorRules;
import java.util.UUID;

@Table(name = Tables.AUTHORS)
@Getter
@Setter
@Entity
@NoArgsConstructor
public class AuthorEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "full_name", length = AuthorRules.AUTHOR_FULL_NAME_MAX_LENGTH, nullable = false)
    private String fullName;
}
