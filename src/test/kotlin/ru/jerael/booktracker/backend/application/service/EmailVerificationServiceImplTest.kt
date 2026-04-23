package ru.jerael.booktracker.backend.application.service

import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.extension.ExtendWith
import ru.jerael.booktracker.backend.domain.exception.NotFoundException
import ru.jerael.booktracker.backend.domain.exception.TooManyRequestsException
import ru.jerael.booktracker.backend.domain.mail.VerificationMailMessage
import ru.jerael.booktracker.backend.domain.model.verification.EmailVerification
import ru.jerael.booktracker.backend.domain.repository.EmailVerificationRepository
import ru.jerael.booktracker.backend.domain.smtp.SmtpService
import ru.jerael.booktracker.backend.domain.validator.VerificationTokenValidator
import ru.jerael.booktracker.backend.domain.verification.VerificationTokenGenerator
import ru.jerael.booktracker.backend.factory.verification.EmailVerificationDomainFactory
import java.time.Instant
import java.util.*

@ExtendWith(MockKExtension::class)
class EmailVerificationServiceImplTest {
    
    @MockK
    private lateinit var verificationTokenGenerator: VerificationTokenGenerator
    
    @MockK
    private lateinit var emailVerificationRepository: EmailVerificationRepository
    
    @MockK(relaxed = true)
    private lateinit var smtpService: SmtpService
    
    @MockK
    private lateinit var verificationTokenValidator: VerificationTokenValidator
    
    @InjectMockKs
    private lateinit var service: EmailVerificationServiceImpl
    
    @Test
    fun `when code not existing, initiate should generate and save new one`() {
        val payload = EmailVerificationDomainFactory.createEmailVerificationInitiation()
        val verificationToken = EmailVerificationDomainFactory.createVerificationToken()
        
        every { emailVerificationRepository.findByUserIdAndType(payload.userId, payload.type) } returns Optional.empty()
        every { verificationTokenGenerator.generate() } returns verificationToken
        every { emailVerificationRepository.save(any()) } returns mockk()
        
        service.initiate(payload)
        
        verify { verificationTokenGenerator.generate() }
        
        val emailVerificationSlot = slot<EmailVerification>()
        verify { emailVerificationRepository.save(capture(emailVerificationSlot)) }
        
        with(emailVerificationSlot.captured) {
            assertEquals(payload.email, email)
            assertEquals(verificationToken.value, token)
            assertNotNull(expiresAt)
        }
        
        verify {
            smtpService.sendEmail(eq(payload.email), any(VerificationMailMessage::class))
        }
    }
    
    @Test
    fun `when valid code exists, initiate should resend same code without generating new one`() {
        val payload = EmailVerificationDomainFactory.createEmailVerificationInitiation()
        
        val userId = payload.userId
        val email = payload.email
        val type = payload.type
        
        val oldToken = "old token"
        
        val found = EmailVerificationDomainFactory.createEmailVerification(
            userId = userId,
            email = email,
            type = type,
            token = oldToken,
            createdAt = Instant.now().minusSeconds(120)
        )
        
        every { emailVerificationRepository.findByUserIdAndType(userId, type) } returns Optional.of(found)
        
        service.initiate(payload)
        
        val verificationMailMessageSlot = slot<VerificationMailMessage>()
        verify { smtpService.sendEmail(eq(email), capture(verificationMailMessageSlot)) }
        
        assertEquals(oldToken, verificationMailMessageSlot.captured.token.value)
        
        verify(exactly = 0) { verificationTokenGenerator.generate() }
        verify(exactly = 0) { emailVerificationRepository.save(any()) }
    }
    
    @Test
    fun `when resending too fast, initiate should throw TooManyRequestsException`() {
        val payload = EmailVerificationDomainFactory.createEmailVerificationInitiation()
        
        val userId = payload.userId
        val email = payload.email
        val type = payload.type
        
        val found = EmailVerificationDomainFactory.createEmailVerification(
            userId = userId,
            email = email,
            type = type,
            createdAt = Instant.now().minusSeconds(30)
        )
        
        every { emailVerificationRepository.findByUserIdAndType(userId, type) } returns Optional.of(found)
        
        assertThrows(TooManyRequestsException::class.java) { service.initiate(payload) }
        
        verify(exactly = 0) { smtpService.sendEmail(any(), any()) }
        verify(exactly = 0) { emailVerificationRepository.deleteByUserIdAndType(any(), any()) }
        verify(exactly = 0) { verificationTokenGenerator.generate() }
        verify(exactly = 0) { emailVerificationRepository.save(any()) }
    }
    
    @Test
    fun `confirm should validate and delete existing code`() {
        val payload = EmailVerificationDomainFactory.createEmailVerificationConfirmation()
        val existing = mockk<EmailVerification>()
        
        val userId = payload.userId
        val type = payload.type
        
        every { emailVerificationRepository.findByUserIdAndType(userId, type) } returns Optional.of(existing)
        every { verificationTokenValidator.validate(any(), any()) } just Runs
        every { emailVerificationRepository.deleteByUserIdAndType(any(), any()) } just Runs
        
        val result = service.confirm(payload)
        
        assertNotNull(result)
        
        verify { verificationTokenValidator.validate(existing, payload.token) }
        verify { emailVerificationRepository.deleteByUserIdAndType(userId, type) }
    }
    
    @Test
    fun `when record does not exist in database, confirm should throw NotFoundException`() {
        val payload = EmailVerificationDomainFactory.createEmailVerificationConfirmation()
        
        every { emailVerificationRepository.findByUserIdAndType(any(), any()) } returns Optional.empty()
        
        assertThrows(NotFoundException::class.java) { service.confirm(payload) }
        
        verify(exactly = 0) { verificationTokenValidator.validate(any(), any()) }
    }
}