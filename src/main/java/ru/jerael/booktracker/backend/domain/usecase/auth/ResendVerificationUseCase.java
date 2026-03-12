package ru.jerael.booktracker.backend.domain.usecase.auth;

import ru.jerael.booktracker.backend.domain.model.auth.ResendVerification;
import ru.jerael.booktracker.backend.domain.model.auth.ResendVerificationResult;

public interface ResendVerificationUseCase {
    ResendVerificationResult execute(ResendVerification data);
}
