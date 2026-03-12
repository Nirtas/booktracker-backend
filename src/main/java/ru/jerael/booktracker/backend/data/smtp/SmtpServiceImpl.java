package ru.jerael.booktracker.backend.data.smtp;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.jerael.booktracker.backend.data.smtp.config.SmtpProperties;
import ru.jerael.booktracker.backend.domain.mail.MailMessage;
import ru.jerael.booktracker.backend.domain.smtp.SmtpService;

@Service
@RequiredArgsConstructor
public class SmtpServiceImpl implements SmtpService {
    private final JavaMailSender mailSender;
    private final SmtpProperties properties;

    @Override
    @Async
    public void sendEmail(String to, MailMessage message) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(message.subject());
        simpleMailMessage.setText(message.body());
        String from = "BookTracker <" + properties.getFrom() + ">";
        simpleMailMessage.setFrom(from);

        mailSender.send(simpleMailMessage);
    }
}
