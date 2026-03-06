package ru.jerael.booktracker.backend.data.db.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.jerael.booktracker.backend.data.db.constant.Tables;
import ru.jerael.booktracker.backend.domain.constant.UserRules;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = Tables.USERS)
@Getter
@Setter
@NoArgsConstructor
public class UserEntity {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "email", length = UserRules.EMAIL_MAX_LENGTH, nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", length = UserRules.PASSWORD_HASH_MAX_LENGTH, nullable = false)
    private String passwordHash;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
