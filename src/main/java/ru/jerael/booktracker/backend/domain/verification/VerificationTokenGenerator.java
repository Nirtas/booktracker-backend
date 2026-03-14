package ru.jerael.booktracker.backend.domain.verification;

import ru.jerael.booktracker.backend.domain.model.verification.VerificationToken;

public interface VerificationTokenGenerator {
    VerificationToken generate();
}
