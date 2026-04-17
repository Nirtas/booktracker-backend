package ru.jerael.booktracker.backend.application.validator

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import ru.jerael.booktracker.backend.domain.exception.ValidationException
import ru.jerael.booktracker.backend.factory.verification.EmailVerificationDomainFactory
import java.time.Instant

class VerificationTokenValidatorImplTest {
    private val validator = VerificationTokenValidatorImpl()
    
    @Test
    fun `when token is valid, validate should not throw Exception`() {
        val token = "123456"
        val verification = EmailVerificationDomainFactory.createEmailVerification(token = token)
        
        assertDoesNotThrow { validator.validate(verification, token) }
    }
    
    @Test
    fun `when token is expired, validate should throw ValidationException`() {
        val token = "123456"
        val verification = EmailVerificationDomainFactory.createEmailVerification(
            token = token,
            expiresAt = Instant.now().minusSeconds(60)
        )
        
        val exception = assertThrows(ValidationException::class.java) {
            validator.validate(verification, token)
        }
        
        assertThat(exception.errors).anyMatch { it.message.contains("expired") }
    }
    
    @Test
    fun `when token mismatched, validate should throw ValidationException`() {
        val verification = EmailVerificationDomainFactory.createEmailVerification(
            expiresAt = Instant.now().minusSeconds(60)
        )
        
        val exception = assertThrows(ValidationException::class.java) {
            validator.validate(verification, "invalid token")
        }
        
        assertThat(exception.errors).anyMatch { it.message.contains("invalid") }
    }
    
    @Test
    fun `when token is expired and mismatched, validate should throw multiple validation errors`() {
        val verification = EmailVerificationDomainFactory.createEmailVerification(
            expiresAt = Instant.now().minusSeconds(60)
        )
        
        val exception = assertThrows(ValidationException::class.java) {
            validator.validate(verification, "invalid token")
        }
        
        assertEquals(2, exception.errors.size)
    }
}