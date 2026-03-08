package ru.jerael.booktracker.backend.domain.model.verification;

import java.util.UUID;

public record EmailVerificationInitiation(
    UUID userId,
    String email,
    VerificationType type
) {}
