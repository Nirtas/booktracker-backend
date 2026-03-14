package ru.jerael.booktracker.backend.application.usecase.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.jerael.booktracker.backend.domain.exception.ValidationException;
import ru.jerael.booktracker.backend.domain.model.auth.ConfirmRegistration;
import ru.jerael.booktracker.backend.domain.model.auth.TokenPair;
import ru.jerael.booktracker.backend.domain.model.user.User;
import ru.jerael.booktracker.backend.domain.model.verification.EmailVerificationConfirmation;
import ru.jerael.booktracker.backend.domain.model.verification.VerificationType;
import ru.jerael.booktracker.backend.domain.repository.UserRepository;
import ru.jerael.booktracker.backend.domain.service.token.AuthTokenService;
import ru.jerael.booktracker.backend.domain.validator.AuthValidator;
import ru.jerael.booktracker.backend.domain.verification.EmailVerificationService;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmRegistrationUseCaseImplTest {

    @Mock
    private AuthValidator authValidator;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailVerificationService emailVerificationService;

    @Mock
    private AuthTokenService authTokenService;

    @InjectMocks
    private ConfirmRegistrationUseCaseImpl useCase;

    private final UUID userId = UUID.fromString("ee39af7a-a073-4473-878a-1aae34e98bb7");
    private final String token = "123456";
    private final String email = "test@example.com";
    private final String passwordHash = "password hash";
    private final ConfirmRegistration confirmRegistration = new ConfirmRegistration(userId, token);

    @Test
    void execute_ShouldVerifyUserAndDeleteCodeAndReturnTokens() {
        User unverifiedUser = new User(userId, email, passwordHash, false, Instant.now());
        TokenPair tokenPair = new TokenPair("access token", "refresh token");
        when(userRepository.findById(userId)).thenReturn(Optional.of(unverifiedUser));
        when(authTokenService.issueTokens(userId)).thenReturn(tokenPair);

        TokenPair result = useCase.execute(confirmRegistration);

        verify(authValidator).validateRegistrationConfirmation(confirmRegistration);

        ArgumentCaptor<EmailVerificationConfirmation> confirmCaptor =
            ArgumentCaptor.forClass(EmailVerificationConfirmation.class);
        verify(emailVerificationService).confirm(confirmCaptor.capture());
        assertEquals(userId, confirmCaptor.getValue().userId());
        assertEquals(token, confirmCaptor.getValue().token());
        assertEquals(VerificationType.REGISTRATION, confirmCaptor.getValue().type());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertTrue(userCaptor.getValue().isVerified());

        assertEquals(tokenPair, result);
    }

    @Test
    void execute_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> useCase.execute(confirmRegistration));
    }

    @Test
    void execute_WhenUserAlreadyVerified_ShouldThrowException() {
        User verifiedUser = new User(userId, email, passwordHash, true, Instant.now());
        when(userRepository.findById(userId)).thenReturn(Optional.of(verifiedUser));

        assertThrows(ValidationException.class, () -> useCase.execute(confirmRegistration));

        verifyNoInteractions(emailVerificationService);
        verifyNoInteractions(authTokenService);
    }
}