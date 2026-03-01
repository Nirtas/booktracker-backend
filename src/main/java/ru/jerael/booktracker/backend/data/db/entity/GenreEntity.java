package ru.jerael.booktracker.backend.data.db.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.jerael.booktracker.backend.data.db.constant.Tables;
import ru.jerael.booktracker.backend.domain.constant.GenreRules;

@Entity
@Table(name = Tables.GENRES)
@Getter
@Setter
@NoArgsConstructor
public class GenreEntity {
    @Id
    @Column(name = "genre_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "genre_name", length = GenreRules.GENRE_NAME_MAX_LENGTH, nullable = false, unique = true)
    private String name;
}
