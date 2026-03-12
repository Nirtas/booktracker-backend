package ru.jerael.booktracker.backend.domain.model.auth;

import ru.jerael.booktracker.backend.domain.model.verification.VerificationType;
import java.util.UUID;

public record ResendVerification(
    UUID userId,
    VerificationType type
) {}
