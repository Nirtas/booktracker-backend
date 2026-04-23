package ru.jerael.booktracker.backend.application.validator

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.domain.constant.EmailVerificationRules
import ru.jerael.booktracker.backend.domain.exception.ValidationException
import ru.jerael.booktracker.backend.domain.exception.code.CommonValidationErrorCode
import ru.jerael.booktracker.backend.factory.auth.AuthDomainFactory

class AuthValidatorImplTest {
    private val fieldValidator = FieldValidatorImpl()
    private val authValidator = AuthValidatorImpl(fieldValidator)
    
    @Test
    fun `when valid, validateRegistrationConfirmation should not throw Exception`() {
        val data = AuthDomainFactory.createConfirmRegistration()
        
        assertDoesNotThrow { authValidator.validateRegistrationConfirmation(data) }
    }
    
    @Test
    fun `when userId is null, validateRegistrationConfirmation should throw ValidationException`() {
        val data = AuthDomainFactory.createConfirmRegistration(userId = null)
        
        val exception = assertThrows(ValidationException::class.java) {
            authValidator.validateRegistrationConfirmation(data)
        }
        
        assertThat(exception.errors).anyMatch { it.field.equals("userId") }
    }
    
    @Test
    fun `when token is blank, validateRegistrationConfirmation should throw ValidationException`() {
        val data = AuthDomainFactory.createConfirmRegistration(token = "  ")
        
        val exception = assertThrows(ValidationException::class.java) {
            authValidator.validateRegistrationConfirmation(data)
        }
        
        assertThat(exception.errors).anyMatch { it.field.equals("token") }
    }
    
    @Test
    fun `when token too short, validateRegistrationConfirmation should throw ValidationException`() {
        val data = AuthDomainFactory.createConfirmRegistration(token = "1")
        
        val exception = assertThrows(ValidationException::class.java) {
            authValidator.validateRegistrationConfirmation(data)
        }
        
        assertThat(exception.errors).anyMatch { it.code.equals(CommonValidationErrorCode.FIELD_TOO_SHORT.name) }
    }
    
    @Test
    fun `when token too long, validateRegistrationConfirmation should throw ValidationException`() {
        val token = "a".repeat(EmailVerificationRules.TOKEN_MAX_LENGTH + 1)
        val data = AuthDomainFactory.createConfirmRegistration(token = token)
        
        val exception = assertThrows(ValidationException::class.java) {
            authValidator.validateRegistrationConfirmation(data)
        }
        
        assertThat(exception.errors).anyMatch { it.code.equals(CommonValidationErrorCode.FIELD_TOO_LONG.name) }
    }
    
    @Test
    fun `when valid, validateRefreshTokenPayload should not throw Exception`() {
        val data = AuthDomainFactory.createRefreshTokenPayload()
        
        assertDoesNotThrow { authValidator.validateRefreshTokenPayload(data) }
    }
    
    @Test
    fun `when refreshToken is blank, validateRefreshTokenPayload should throw ValidationException`() {
        val data = AuthDomainFactory.createRefreshTokenPayload(refreshToken = "  ")
        
        val exception = assertThrows(ValidationException::class.java) {
            authValidator.validateRefreshTokenPayload(data)
        }
        
        assertThat(exception.errors).anyMatch { it.field.equals("refreshToken") }
    }
    
    @Test
    fun `when valid, validateLogoutPayload should not throw Exception`() {
        val data = AuthDomainFactory.createLogoutPayload()
        
        assertDoesNotThrow { authValidator.validateLogoutPayload(data) }
    }
    
    @Test
    fun `when refreshToken is blank, validateLogoutPayload should throw ValidationException`() {
        val data = AuthDomainFactory.createLogoutPayload(refreshToken = "  ")
        
        val exception = assertThrows(ValidationException::class.java) {
            authValidator.validateLogoutPayload(data)
        }
        
        assertThat(exception.errors).anyMatch { it.field.equals("refreshToken") }
    }
    
    @Test
    fun `when valid, validateResendVerification should not throw Exception`() {
        val data = AuthDomainFactory.createResendVerification()
        
        assertDoesNotThrow { authValidator.validateResendVerification(data) }
    }
    
    @Test
    fun `when userId is null, validateResendVerification should throw ValidationException`() {
        val data = AuthDomainFactory.createResendVerification(userId = null)
        
        val exception = assertThrows(ValidationException::class.java) {
            authValidator.validateResendVerification(data)
        }
        
        assertThat(exception.errors).anyMatch { it.field.equals("userId") }
    }
    
    @Test
    fun `when type is null, validateResendVerification should throw ValidationException`() {
        val data = AuthDomainFactory.createResendVerification(type = null)
        
        val exception = assertThrows(ValidationException::class.java) {
            authValidator.validateResendVerification(data)
        }
        
        assertThat(exception.errors).anyMatch { it.field.equals("type") }
    }
}