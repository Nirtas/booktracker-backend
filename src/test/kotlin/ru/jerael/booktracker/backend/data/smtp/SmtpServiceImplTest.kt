package ru.jerael.booktracker.backend.data.smtp

import ch.martinelli.oss.testcontainers.mailpit.MailpitClient
import ch.martinelli.oss.testcontainers.mailpit.Message
import ch.martinelli.oss.testcontainers.mailpit.assertions.MailpitAssertions.assertThat
import com.ninjasquad.springmockk.MockkBean
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import ru.jerael.booktracker.backend.config.TestcontainersConfiguration
import ru.jerael.booktracker.backend.domain.mail.VerificationMailMessage
import ru.jerael.booktracker.backend.domain.storage.BookCoverStorage
import ru.jerael.booktracker.backend.factory.verification.EmailVerificationDomainFactory

@SpringBootTest
@Import(TestcontainersConfiguration::class)
class SmtpServiceImplTest {
    
    @MockkBean
    private lateinit var bookCoverStorage: BookCoverStorage
    
    @Autowired
    private lateinit var mailpitClient: MailpitClient
    
    @Autowired
    private lateinit var smtpService: SmtpServiceImpl
    
    @Test
    fun `sendEmail should send verification message`() {
        val verificationToken = EmailVerificationDomainFactory.createVerificationToken()
        val mailMessage = VerificationMailMessage(verificationToken)
        
        smtpService.sendEmail("user@example.com", mailMessage)
        
        var messages = emptyList<Message>()
        repeat(20) {
            messages = mailpitClient.allMessages
            if (!messages.isEmpty()) return@repeat
            Thread.sleep(1000)
        }
        
        val message = messages[0]
        assertNotNull(message)
        assertThat(message)
            .hasSubject("Your verification code for BookTracker")
            .hasSnippetContaining(verificationToken.value)
            .hasSnippetContaining("${verificationToken.expiry.toMinutes()} minutes")
    }
}