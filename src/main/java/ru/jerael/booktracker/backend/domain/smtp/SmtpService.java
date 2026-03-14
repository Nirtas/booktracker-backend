package ru.jerael.booktracker.backend.domain.smtp;

import ru.jerael.booktracker.backend.domain.mail.MailMessage;

public interface SmtpService {
    void sendEmail(String to, MailMessage message);
}
