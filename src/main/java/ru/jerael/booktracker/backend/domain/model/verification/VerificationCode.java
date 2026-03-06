package ru.jerael.booktracker.backend.domain.model.verification;

import java.time.Duration;

public record VerificationCode(
    String value,
    Duration expiry
) {}
