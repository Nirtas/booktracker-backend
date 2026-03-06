package ru.jerael.booktracker.backend.domain.verification;

import ru.jerael.booktracker.backend.domain.model.verification.VerificationCode;

public interface VerificationCodeGenerator {
    VerificationCode generate();
}
