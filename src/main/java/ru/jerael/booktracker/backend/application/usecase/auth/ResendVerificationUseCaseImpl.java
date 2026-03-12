package ru.jerael.booktracker.backend.application.usecase.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jerael.booktracker.backend.domain.exception.factory.UserExceptionFactory;
import ru.jerael.booktracker.backend.domain.model.auth.ResendVerification;
import ru.jerael.booktracker.backend.domain.model.auth.ResendVerificationResult;
import ru.jerael.booktracker.backend.domain.model.user.User;
import ru.jerael.booktracker.backend.domain.model.verification.EmailVerificationInitiation;
import ru.jerael.booktracker.backend.domain.model.verification.VerificationType;
import ru.jerael.booktracker.backend.domain.repository.UserRepository;
import ru.jerael.booktracker.backend.domain.usecase.auth.ResendVerificationUseCase;
import ru.jerael.booktracker.backend.domain.validator.AuthValidator;
import ru.jerael.booktracker.backend.domain.verification.EmailVerificationService;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResendVerificationUseCaseImpl implements ResendVerificationUseCase {
    private final AuthValidator authValidator;
    private final UserRepository userRepository;
    private final EmailVerificationService emailVerificationService;

    @Override
    @Transactional
    public ResendVerificationResult execute(ResendVerification data) {
        authValidator.validateResendVerification(data);
        UUID userId = data.userId();
        VerificationType type = data.type();

        User user = userRepository.findById(userId).orElseThrow(() -> UserExceptionFactory.userNotFound(userId));
        if (user.isVerified() && type == VerificationType.REGISTRATION) {
            throw UserExceptionFactory.userAlreadyVerified(userId);
        }
        EmailVerificationInitiation initiation = new EmailVerificationInitiation(userId, user.email(), type);
        Instant expiresAt = emailVerificationService.initiate(initiation);
        return new ResendVerificationResult(userId, user.email(), expiresAt);
    }
}
