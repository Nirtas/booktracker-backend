package ru.jerael.booktracker.backend.domain.mail;

import ru.jerael.booktracker.backend.domain.model.verification.VerificationToken;

public class VerificationMailMessage implements MailMessage {
    private final VerificationToken token;

    public VerificationMailMessage(VerificationToken token) {
        this.token = token;
    }

    @Override
    public String subject() {
        return "Your verification code for BookTracker";
    }

    @Override
    public String body() {
        return String.format(
            """
                    Hello!
                    \s
                    Thank you for registration at BookTracker.
                    Your verification code is: %s
                    \s
                    This code will expire in %s minutes.
                    \s
                    If you did not request this, please ignore this email.
                \s""",
            token.value(),
            token.expiry().toMinutes()
        );
    }
}
