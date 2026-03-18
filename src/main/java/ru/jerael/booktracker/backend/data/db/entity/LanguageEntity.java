package ru.jerael.booktracker.backend.data.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.jerael.booktracker.backend.data.db.constant.Tables;
import ru.jerael.booktracker.backend.domain.constant.LanguageRules;

@Table(name = Tables.LANGUAGES)
@Getter
@Setter
@Entity
@NoArgsConstructor
public class LanguageEntity {
    @Id
    @Column(name = "code", length = LanguageRules.LANGUAGE_CODE_LENGTH, nullable = false)
    private String code;

    @Column(name = "name", length = LanguageRules.LANGUAGE_NAME_MAX_LENGTH, nullable = false)
    private String name;
}
