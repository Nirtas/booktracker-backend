package ru.jerael.booktracker.backend.domain.verification;

import ru.jerael.booktracker.backend.domain.model.verification.EmailVerification;
import ru.jerael.booktracker.backend.domain.model.verification.EmailVerificationConfirmation;
import ru.jerael.booktracker.backend.domain.model.verification.EmailVerificationInitiation;
import java.time.Instant;

public interface EmailVerificationService {
    Instant initiate(EmailVerificationInitiation payload);

    EmailVerification confirm(EmailVerificationConfirmation payload);
}
