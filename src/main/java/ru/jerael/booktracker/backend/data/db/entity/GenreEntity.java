package ru.jerael.booktracker.backend.data.db.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.jerael.booktracker.backend.data.db.DbConstants;

@Entity
@Table(name = DbConstants.TABLE_GENRES)
@Getter
@Setter
@NoArgsConstructor
public class GenreEntity {
    @Id
    @Column(name = "genre_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "genre_name", length = 128, nullable = false, unique = true)
    private String name;
}
