package ru.jerael.booktracker.backend.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.jerael.booktracker.backend.domain.exception.NotFoundException;
import ru.jerael.booktracker.backend.domain.exception.TooManyRequestsException;
import ru.jerael.booktracker.backend.domain.mail.VerificationMailMessage;
import ru.jerael.booktracker.backend.domain.model.verification.*;
import ru.jerael.booktracker.backend.domain.repository.EmailVerificationRepository;
import ru.jerael.booktracker.backend.domain.smtp.SmtpService;
import ru.jerael.booktracker.backend.domain.validator.VerificationTokenValidator;
import ru.jerael.booktracker.backend.domain.verification.VerificationTokenGenerator;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceImplTest {

    @Mock
    private VerificationTokenGenerator verificationTokenGenerator;

    @Mock
    private EmailVerificationRepository emailVerificationRepository;

    @Mock
    private SmtpService smtpService;

    @Mock
    private VerificationTokenValidator verificationTokenValidator;

    @InjectMocks
    private EmailVerificationServiceImpl service;

    private final UUID userId = UUID.fromString("2c5781ea-1bc2-4561-a83d-26106df2526e");
    private final String email = "test@example.com";
    private final VerificationType type = VerificationType.REGISTRATION;
    private final String token = "123456";

    @Test
    void initiate_WhenNoExistingCode_ShouldGenerateAndSaveNewOne() {
        EmailVerificationInitiation payload = new EmailVerificationInitiation(userId, email, type);
        VerificationToken verificationToken = new VerificationToken(token, Duration.ofMinutes(10L));
        when(emailVerificationRepository.findByUserIdAndType(userId, type)).thenReturn(Optional.empty());
        when(verificationTokenGenerator.generate()).thenReturn(verificationToken);

        service.initiate(payload);

        verify(verificationTokenGenerator).generate();

        ArgumentCaptor<EmailVerification> captor = ArgumentCaptor.forClass(EmailVerification.class);
        verify(emailVerificationRepository).save(captor.capture());

        EmailVerification savedVerification = captor.getValue();
        assertEquals(email, savedVerification.email());
        assertEquals(token, savedVerification.token());
        assertNotNull(savedVerification.expiresAt());

        verify(smtpService).sendEmail(eq(email), any(VerificationMailMessage.class));
    }

    @Test
    void initiate_WhenValidCodeExists_ShouldResendSameCodeWithoutGeneratingNewOne() {
        EmailVerificationInitiation payload = new EmailVerificationInitiation(userId, email, type);
        EmailVerification found = new EmailVerification(
            UUID.randomUUID(),
            userId,
            email,
            type,
            "old token",
            Instant.now().plus(Duration.ofMinutes(8)),
            Instant.now().minus(Duration.ofMinutes(2))
        );
        when(emailVerificationRepository.findByUserIdAndType(userId, type)).thenReturn(Optional.of(found));

        service.initiate(payload);

        ArgumentCaptor<VerificationMailMessage> mailCaptor = ArgumentCaptor.forClass(VerificationMailMessage.class);
        verify(smtpService).sendEmail(eq(email), mailCaptor.capture());
        assertEquals("old token", mailCaptor.getValue().getToken().value());

        verify(verificationTokenGenerator, never()).generate();
        verify(emailVerificationRepository, never()).save(any());
    }

    @Test
    void initiate_WhenResendingTooFast_ShouldThrowException() {
        EmailVerificationInitiation payload = new EmailVerificationInitiation(userId, email, type);
        EmailVerification found = new EmailVerification(
            UUID.randomUUID(),
            userId,
            email,
            type,
            token,
            Instant.now().plus(Duration.ofMinutes(10)),
            Instant.now().minus(Duration.ofSeconds(30))
        );
        when(emailVerificationRepository.findByUserIdAndType(userId, type)).thenReturn(Optional.of(found));

        assertThrows(TooManyRequestsException.class, () -> service.initiate(payload));

        verify(smtpService, never()).sendEmail(any(), any());
        verify(emailVerificationRepository, never()).deleteByUserIdAndType(any(), any());
        verify(verificationTokenGenerator, never()).generate();
        verify(emailVerificationRepository, never()).save(any());
    }

    @Test
    void confirm_ShouldValidateAndDeleteExistingCode() {
        EmailVerificationConfirmation payload = new EmailVerificationConfirmation(userId, token, type);
        EmailVerification existing = mock(EmailVerification.class);
        when(emailVerificationRepository.findByUserIdAndType(userId, type)).thenReturn(Optional.of(existing));

        EmailVerification result = service.confirm(payload);

        assertNotNull(result);
        verify(verificationTokenValidator).validate(existing, token);
        verify(emailVerificationRepository).deleteByUserIdAndType(userId, type);
    }

    @Test
    void confirm_ShouldThrowNotFoundException_WhenRecordDoesNotExistInDb() {
        EmailVerificationConfirmation payload = new EmailVerificationConfirmation(userId, token, type);
        when(emailVerificationRepository.findByUserIdAndType(userId, type)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.confirm(payload));
        verify(verificationTokenValidator, never()).validate(any(), any());
    }
}