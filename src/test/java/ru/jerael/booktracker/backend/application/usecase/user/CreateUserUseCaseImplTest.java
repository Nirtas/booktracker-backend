package ru.jerael.booktracker.backend.application.usecase.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.jerael.booktracker.backend.domain.exception.AlreadyExistsException;
import ru.jerael.booktracker.backend.domain.hasher.PasswordHasher;
import ru.jerael.booktracker.backend.domain.model.user.User;
import ru.jerael.booktracker.backend.domain.model.user.UserCreation;
import ru.jerael.booktracker.backend.domain.model.user.UserCreationResult;
import ru.jerael.booktracker.backend.domain.model.verification.EmailVerificationInitiation;
import ru.jerael.booktracker.backend.domain.model.verification.VerificationType;
import ru.jerael.booktracker.backend.domain.repository.UserRepository;
import ru.jerael.booktracker.backend.domain.validator.UserValidator;
import ru.jerael.booktracker.backend.domain.verification.EmailVerificationService;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private UserValidator userValidator;

    @Mock
    private EmailVerificationService emailVerificationService;

    @InjectMocks
    private CreateUserUseCaseImpl useCase;

    private final String email = "test@example.com";
    private final String password = "Password123!";
    private final String passwordHash = "password hash";
    private final UUID id = UUID.fromString("ee39af7a-a073-4473-878a-1aae34e98bb7");

    private final UserCreation userCreation = new UserCreation(email, password);
    private final User user = new User(id, email, passwordHash, false, Instant.now());
    private final Instant expiresAt = Instant.now().plusSeconds(1000L);

    @Test
    void execute_WhenEmailIsNotTaken_ShouldCreateUserAndInitiateVerification() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordHasher.hash(password)).thenReturn(passwordHash);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(emailVerificationService.initiate(any(EmailVerificationInitiation.class))).thenReturn(expiresAt);

        UserCreationResult result = useCase.execute(userCreation);

        assertNotNull(result);
        assertEquals(id, result.userId());
        assertEquals(email, result.email());
        assertEquals(expiresAt, result.expiresAt());

        verify(userValidator).validateCreation(userCreation);
        verify(userRepository).findByEmail(email);
        verify(passwordHasher).hash(password);
        verify(userRepository).save(any(User.class));

        ArgumentCaptor<EmailVerificationInitiation> captor = ArgumentCaptor.forClass(EmailVerificationInitiation.class);
        verify(emailVerificationService).initiate(captor.capture());
        assertEquals(id, captor.getValue().userId());
        assertEquals(email, captor.getValue().email());
        assertEquals(VerificationType.REGISTRATION, captor.getValue().type());
    }

    @Test
    void execute_WhenEmailIsTaken_ShouldThrowException() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        assertThrows(AlreadyExistsException.class, () -> useCase.execute(userCreation));

        verify(userValidator).validateCreation(userCreation);
        verify(userRepository).findByEmail(email);
        verifyNoInteractions(passwordHasher);
        verifyNoInteractions(emailVerificationService);
    }
}