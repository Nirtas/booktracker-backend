package ru.jerael.booktracker.backend.domain.validator;

import ru.jerael.booktracker.backend.domain.model.verification.EmailVerification;

public interface VerificationTokenValidator {
    void validate(EmailVerification emailVerification, String code);
}
