package ru.jerael.booktracker.backend.application.usecase.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import ru.jerael.booktracker.backend.application.annotation.UseCase;
import ru.jerael.booktracker.backend.domain.exception.factory.UserExceptionFactory;
import ru.jerael.booktracker.backend.domain.hasher.PasswordHasher;
import ru.jerael.booktracker.backend.domain.model.auth.TokenPair;
import ru.jerael.booktracker.backend.domain.model.auth.UserLogin;
import ru.jerael.booktracker.backend.domain.model.user.User;
import ru.jerael.booktracker.backend.domain.repository.UserRepository;
import ru.jerael.booktracker.backend.domain.service.token.AuthTokenService;
import ru.jerael.booktracker.backend.domain.usecase.auth.LoginUserUseCase;
import ru.jerael.booktracker.backend.domain.validator.AuthValidator;

@UseCase
@RequiredArgsConstructor
public class LoginUserUseCaseImpl implements LoginUserUseCase {
    private final AuthValidator authValidator;
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final AuthTokenService authTokenService;

    @Override
    @Transactional
    public TokenPair execute(UserLogin data) {
        authValidator.validateLogin(data);
        String email = data.email();
        String password = data.password();
        User user = userRepository.findByEmail(email).orElseThrow(UserExceptionFactory::invalidCredentials);
        if (!passwordHasher.verify(password, user.passwordHash())) {
            throw UserExceptionFactory.invalidCredentials();
        }
        if (!user.isVerified()) {
            throw UserExceptionFactory.userNotVerified(email);
        }
        return authTokenService.issueTokens(user.id());
    }
}
