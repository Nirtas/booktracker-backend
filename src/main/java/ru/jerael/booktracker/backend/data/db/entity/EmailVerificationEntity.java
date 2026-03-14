package ru.jerael.booktracker.backend.data.db.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.jerael.booktracker.backend.data.db.constant.Tables;
import ru.jerael.booktracker.backend.domain.constant.UserRules;
import ru.jerael.booktracker.backend.domain.constant.EmailVerificationRules;
import ru.jerael.booktracker.backend.domain.model.verification.VerificationType;
import java.time.Instant;
import java.util.UUID;

@Table(name = Tables.EMAIL_VERIFICATIONS)
@Setter
@Getter
@Entity
@NoArgsConstructor
public class EmailVerificationEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "email", length = UserRules.EMAIL_MAX_LENGTH, nullable = false)
    private String email;

    @Column(name = "verification_type", length = EmailVerificationRules.TYPE_MAX_LENGTH, nullable = false)
    @Enumerated(EnumType.STRING)
    private VerificationType type;

    @Column(name = "token", length = EmailVerificationRules.TOKEN_MAX_LENGTH, nullable = false)
    private String token;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
