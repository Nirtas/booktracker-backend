package ru.jerael.booktracker.backend.data.db.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.jerael.booktracker.backend.data.db.constant.Tables;
import ru.jerael.booktracker.backend.domain.constant.AuthRules;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Table(name = Tables.REFRESH_TOKENS)
@Entity
@NoArgsConstructor
public class RefreshTokenEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "token_hash", length = AuthRules.REFRESH_TOKEN_HASH_MAX_LENGTH, nullable = false)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
}
