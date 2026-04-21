package ru.jerael.booktracker.backend.application.usecase.user;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import ru.jerael.booktracker.backend.application.annotation.UseCase;
import ru.jerael.booktracker.backend.domain.exception.factory.UserExceptionFactory;
import ru.jerael.booktracker.backend.domain.hasher.PasswordHasher;
import ru.jerael.booktracker.backend.domain.model.user.User;
import ru.jerael.booktracker.backend.domain.model.user.UserCreation;
import ru.jerael.booktracker.backend.domain.model.user.UserCreationResult;
import ru.jerael.booktracker.backend.domain.model.verification.EmailVerificationInitiation;
import ru.jerael.booktracker.backend.domain.model.verification.VerificationType;
import ru.jerael.booktracker.backend.domain.repository.UserRepository;
import ru.jerael.booktracker.backend.domain.usecase.user.CreateUserUseCase;
import ru.jerael.booktracker.backend.domain.validator.UserValidator;
import ru.jerael.booktracker.backend.domain.verification.EmailVerificationService;
import java.time.Instant;

@UseCase
@RequiredArgsConstructor
public class CreateUserUseCaseImpl implements CreateUserUseCase {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final UserValidator userValidator;
    private final EmailVerificationService emailVerificationService;

    @Override
    @Transactional
    public UserCreationResult execute(UserCreation data) {
        userValidator.validateCreation(data);
        if (userRepository.findByEmail(data.email()).isPresent()) {
            throw UserExceptionFactory.emailAlreadyExists(data.email());
        }
        String passwordHash = passwordHasher.hash(data.password());
        User newUser = new User(null, data.email(), passwordHash, false, Instant.now());
        User createdUser = userRepository.save(newUser);
        EmailVerificationInitiation initiation = new EmailVerificationInitiation(
            createdUser.id(),
            createdUser.email(),
            VerificationType.REGISTRATION
        );
        Instant expiresAt = emailVerificationService.initiate(initiation);
        return new UserCreationResult(
            createdUser.id(),
            createdUser.email(),
            expiresAt
        );
    }
}
