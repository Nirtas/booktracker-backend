package ru.jerael.booktracker.backend.data.db.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.jerael.booktracker.backend.data.db.constant.Tables;
import ru.jerael.booktracker.backend.domain.constant.PublisherRules;
import java.util.UUID;

@Table(name = Tables.PUBLISHERS)
@Getter
@Setter
@Entity
@NoArgsConstructor
public class PublisherEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", length = PublisherRules.PUBLISHER_NAME_MAX_LENGTH, nullable = false)
    private String name;
}
