package ru.jerael.booktracker.backend.domain.model.verification;

import java.time.Duration;

public record VerificationToken(
    String value,
    Duration expiry
) {}
