package ru.jerael.booktracker.backend.application.usecase.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.jerael.booktracker.backend.domain.exception.NotFoundException;
import ru.jerael.booktracker.backend.domain.exception.ValidationException;
import ru.jerael.booktracker.backend.domain.model.auth.ResendVerification;
import ru.jerael.booktracker.backend.domain.model.auth.ResendVerificationResult;
import ru.jerael.booktracker.backend.domain.model.user.User;
import ru.jerael.booktracker.backend.domain.model.verification.EmailVerificationInitiation;
import ru.jerael.booktracker.backend.domain.model.verification.VerificationType;
import ru.jerael.booktracker.backend.domain.repository.UserRepository;
import ru.jerael.booktracker.backend.domain.validator.AuthValidator;
import ru.jerael.booktracker.backend.domain.verification.EmailVerificationService;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResendVerificationUseCaseImplTest {

    @Mock
    private AuthValidator authValidator;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailVerificationService emailVerificationService;

    @InjectMocks
    private ResendVerificationUseCaseImpl useCase;

    private final UUID userId = UUID.fromString("ee39af7a-a073-4473-878a-1aae34e98bb7");
    private final String email = "test@example.com";
    private final Instant expiresAt = Instant.now().plusSeconds(600);
    private final String passwordHash = "password hash";
    private final ResendVerification data = new ResendVerification(userId, VerificationType.REGISTRATION);

    @Test
    void execute_ShouldReturnResendVerificationResult() {
        User user = new User(userId, email, passwordHash, false, Instant.now());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(emailVerificationService.initiate(any(EmailVerificationInitiation.class))).thenReturn(expiresAt);

        ResendVerificationResult result = useCase.execute(data);

        assertEquals(userId, result.userId());
        assertEquals(email, result.email());
        assertEquals(expiresAt, result.expiresAt());

        verify(authValidator).validateResendVerification(data);

        ArgumentCaptor<EmailVerificationInitiation> initiationCaptor =
            ArgumentCaptor.forClass(EmailVerificationInitiation.class);
        verify(emailVerificationService).initiate(initiationCaptor.capture());

        EmailVerificationInitiation captured = initiationCaptor.getValue();
        assertEquals(userId, captured.userId());
        assertEquals(email, captured.email());
        assertEquals(VerificationType.REGISTRATION, captured.type());
    }

    @Test
    void execute_WhenUserNotFound_ShouldThrowNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> useCase.execute(data));

        verify(emailVerificationService, never()).initiate(any());
    }

    @Test
    void execute_WhenUserAlreadyVerified_ShouldThrowValidationException() {
        User user = new User(userId, email, passwordHash, true, Instant.now());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(ValidationException.class, () -> useCase.execute(data));

        verify(emailVerificationService, never()).initiate(any());
    }
}