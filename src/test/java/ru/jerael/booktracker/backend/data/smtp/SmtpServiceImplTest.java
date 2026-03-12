package ru.jerael.booktracker.backend.data.smtp;

import ch.martinelli.oss.testcontainers.mailpit.MailpitClient;
import ch.martinelli.oss.testcontainers.mailpit.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.jerael.booktracker.backend.config.TestcontainersConfiguration;
import ru.jerael.booktracker.backend.domain.mail.VerificationMailMessage;
import ru.jerael.booktracker.backend.domain.model.verification.VerificationToken;
import ru.jerael.booktracker.backend.domain.storage.BookCoverStorage;
import java.time.Duration;
import java.util.List;
import static ch.martinelli.oss.testcontainers.mailpit.assertions.MailpitAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class SmtpServiceImplTest {

    @MockitoBean
    private BookCoverStorage bookCoverStorage;

    @Autowired
    private SmtpServiceImpl smtpService;

    @Autowired
    private MailpitClient mailpitClient;

    @Test
    void sendEmail_ShouldSendVerificationMessage() throws InterruptedException {
        String to = "user@example.com";
        String code = "123456";
        VerificationToken verificationToken = new VerificationToken(code, Duration.ofMinutes(10L));
        VerificationMailMessage mailMessage = new VerificationMailMessage(verificationToken);

        smtpService.sendEmail(to, mailMessage);

        List<Message> messages = List.of();
        for (int i = 0; i < 20; i++) {
            messages = mailpitClient.getAllMessages();
            if (!messages.isEmpty()) break;
            Thread.sleep(1000);
        }

        Message message = messages.get(0);
        assertNotNull(message);
        assertThat(message)
            .hasSubject("Your verification code for BookTracker")
            .hasSnippetContaining(code)
            .hasSnippetContaining("10 minutes");
    }
}