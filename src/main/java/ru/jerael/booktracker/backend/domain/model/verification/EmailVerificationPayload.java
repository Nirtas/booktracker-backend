package ru.jerael.booktracker.backend.domain.model.verification;

import java.util.UUID;

public record EmailVerificationPayload(
    UUID userId,
    String email,
    VerificationType type
) {}
