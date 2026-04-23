package ru.jerael.booktracker.backend.application.usecase.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import ru.jerael.booktracker.backend.application.annotation.UseCase;
import ru.jerael.booktracker.backend.domain.exception.factory.UserExceptionFactory;
import ru.jerael.booktracker.backend.domain.model.auth.ConfirmRegistration;
import ru.jerael.booktracker.backend.domain.model.auth.TokenPair;
import ru.jerael.booktracker.backend.domain.model.user.User;
import ru.jerael.booktracker.backend.domain.model.verification.EmailVerificationConfirmation;
import ru.jerael.booktracker.backend.domain.model.verification.VerificationType;
import ru.jerael.booktracker.backend.domain.repository.UserRepository;
import ru.jerael.booktracker.backend.domain.service.token.AuthTokenService;
import ru.jerael.booktracker.backend.domain.usecase.auth.ConfirmRegistrationUseCase;
import ru.jerael.booktracker.backend.domain.validator.AuthValidator;
import ru.jerael.booktracker.backend.domain.verification.EmailVerificationService;
import java.util.UUID;

@UseCase
@RequiredArgsConstructor
public class ConfirmRegistrationUseCaseImpl implements ConfirmRegistrationUseCase {
    private final AuthValidator authValidator;
    private final UserRepository userRepository;
    private final EmailVerificationService emailVerificationService;
    private final AuthTokenService authTokenService;

    @Override
    @Transactional
    public TokenPair execute(ConfirmRegistration data) {
        authValidator.validateRegistrationConfirmation(data);
        UUID userId = data.userId();
        User user =
            userRepository.findById(userId).orElseThrow(() -> UserExceptionFactory.userNotFound(userId));
        if (user.isVerified()) {
            throw UserExceptionFactory.userAlreadyVerified(userId);
        }
        EmailVerificationConfirmation emailVerificationConfirmation = new EmailVerificationConfirmation(
            userId,
            data.token(),
            VerificationType.REGISTRATION
        );
        emailVerificationService.confirm(emailVerificationConfirmation);
        User verifiedUser = new User(
            userId,
            user.email(),
            user.passwordHash(),
            true,
            user.createdAt()
        );
        userRepository.save(verifiedUser);
        return authTokenService.issueTokens(userId);
    }
}
