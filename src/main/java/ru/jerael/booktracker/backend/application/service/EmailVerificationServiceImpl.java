package ru.jerael.booktracker.backend.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jerael.booktracker.backend.application.validator.VerificationTokenValidatorImpl;
import ru.jerael.booktracker.backend.domain.exception.factory.EmailVerificationExceptionFactory;
import ru.jerael.booktracker.backend.domain.mail.VerificationMailMessage;
import ru.jerael.booktracker.backend.domain.model.verification.*;
import ru.jerael.booktracker.backend.domain.repository.EmailVerificationRepository;
import ru.jerael.booktracker.backend.domain.smtp.SmtpService;
import ru.jerael.booktracker.backend.domain.verification.EmailVerificationService;
import ru.jerael.booktracker.backend.domain.verification.VerificationTokenGenerator;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {
    private final VerificationTokenGenerator verificationTokenGenerator;
    private final EmailVerificationRepository emailVerificationRepository;
    private final SmtpService smtpService;
    private final VerificationTokenValidatorImpl verificationTokenValidator;

    @Override
    @Transactional
    public void initiate(EmailVerificationInitiation payload) {
        UUID userId = payload.userId();
        String email = payload.email();
        VerificationType type = payload.type();

        emailVerificationRepository.deleteByUserIdAndType(userId, type);

        VerificationToken token = verificationTokenGenerator.generate();

        EmailVerification emailVerification = new EmailVerification(
            null,
            userId,
            email,
            type,
            token.value(),
            Instant.now().plusMillis(token.expiry().toMillis()),
            Instant.now()
        );
        emailVerificationRepository.save(emailVerification);

        smtpService.sendEmail(email, new VerificationMailMessage(token));
    }

    @Override
    @Transactional
    public EmailVerification confirm(EmailVerificationConfirmation payload) {
        UUID userId = payload.userId();
        String token = payload.token();
        VerificationType type = payload.type();

        EmailVerification emailVerification = emailVerificationRepository.findByUserIdAndType(userId, type)
            .orElseThrow(() -> EmailVerificationExceptionFactory.verificationNotFound(userId, type));

        verificationTokenValidator.validate(emailVerification, token);

        emailVerificationRepository.deleteByUserIdAndType(userId, type);

        return emailVerification;
    }
}
