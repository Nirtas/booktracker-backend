package ru.jerael.booktracker.backend.domain.model.verification;

import java.util.UUID;

public record EmailVerificationConfirmation(
    UUID userId,
    String token,
    VerificationType type
) {}
