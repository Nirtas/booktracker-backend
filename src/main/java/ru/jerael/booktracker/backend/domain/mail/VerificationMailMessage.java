package ru.jerael.booktracker.backend.domain.mail;

import ru.jerael.booktracker.backend.domain.model.verification.VerificationCode;

public class VerificationMailMessage implements MailMessage {
    private final VerificationCode code;

    public VerificationMailMessage(VerificationCode code) {
        this.code = code;
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
            code.value(),
            code.expiry().toMinutes()
        );
    }
}
