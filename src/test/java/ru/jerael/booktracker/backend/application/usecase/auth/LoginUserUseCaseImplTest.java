package ru.jerael.booktracker.backend.application.usecase.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.jerael.booktracker.backend.domain.exception.UnauthenticatedException;
import ru.jerael.booktracker.backend.domain.exception.ValidationException;
import ru.jerael.booktracker.backend.domain.hasher.PasswordHasher;
import ru.jerael.booktracker.backend.domain.model.auth.TokenPair;
import ru.jerael.booktracker.backend.domain.model.auth.UserLogin;
import ru.jerael.booktracker.backend.domain.model.user.User;
import ru.jerael.booktracker.backend.domain.repository.UserRepository;
import ru.jerael.booktracker.backend.domain.service.token.AuthTokenService;
import ru.jerael.booktracker.backend.domain.validator.AuthValidator;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUserUseCaseImplTest {

    @Mock
    private AuthValidator authValidator;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private AuthTokenService authTokenService;

    @InjectMocks
    private LoginUserUseCaseImpl useCase;

    private final UUID userId = UUID.fromString("ee39af7a-a073-4473-878a-1aae34e98bb7");
    private final String email = "test@example.com";
    private final String password = "password";
    private final String passwordHash = "password hash";
    private final UserLogin userLogin = new UserLogin(email, password);

    @Test
    void execute_ShouldReturnTokenPair() {
        User user = new User(userId, email, passwordHash, true, Instant.now());
        TokenPair tokenPair = new TokenPair("access token", "refresh token");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordHasher.verify(password, passwordHash)).thenReturn(true);
        when(authTokenService.issueTokens(userId)).thenReturn(tokenPair);

        TokenPair result = useCase.execute(userLogin);

        assertEquals(tokenPair, result);
        verify(authValidator).validateLogin(userLogin);
    }

    @Test
    void execute_WhenUserNotFound_ShouldThrowUnauthenticatedException() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UnauthenticatedException.class, () -> useCase.execute(userLogin));

        verifyNoInteractions(passwordHasher);
        verifyNoInteractions(authTokenService);
    }

    @Test
    void execute_WhenPasswordIsInvalid_ShouldThrowUnauthenticatedException() {
        User user = new User(userId, email, passwordHash, true, Instant.now());
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordHasher.verify(password, passwordHash)).thenReturn(false);

        assertThrows(UnauthenticatedException.class, () -> useCase.execute(userLogin));

        verifyNoInteractions(authTokenService);
    }

    @Test
    void execute_WhenUserNotVerified_ShouldThrowValidationException() {
        User user = new User(userId, email, passwordHash, false, Instant.now());
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordHasher.verify(password, passwordHash)).thenReturn(true);

        assertThrows(ValidationException.class, () -> useCase.execute(userLogin));

        verifyNoInteractions(authTokenService);
    }
}