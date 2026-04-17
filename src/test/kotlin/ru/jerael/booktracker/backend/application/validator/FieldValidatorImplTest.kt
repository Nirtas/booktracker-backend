package ru.jerael.booktracker.backend.application.validator

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import ru.jerael.booktracker.backend.domain.constant.UserRules
import ru.jerael.booktracker.backend.domain.exception.code.CommonValidationErrorCode
import ru.jerael.booktracker.backend.domain.exception.code.EmailErrorCode
import ru.jerael.booktracker.backend.domain.exception.code.PasswordErrorCode

class FieldValidatorImplTest {
    private val fieldValidator = FieldValidatorImpl()
    
    @Test
    fun `when valid, validateEmail should return empty list`() {
        val email = "test@example.com"
        
        val errors = fieldValidator.validateEmail(email)
        
        assertThat(errors).isEmpty()
    }
    
    @Test
    fun `when email is empty, validateEmail should return empty field error`() {
        val email = ""
        
        val errors = fieldValidator.validateEmail(email)
        
        assertThat(errors)
            .extracting("code")
            .contains(CommonValidationErrorCode.FIELD_CANNOT_BE_EMPTY.name)
    }
    
    @Test
    fun `when email too long, validateEmail should return too long error`() {
        val email = "a".repeat(UserRules.EMAIL_MAX_LENGTH + 1)
        
        val errors = fieldValidator.validateEmail(email)
        
        assertThat(errors)
            .extracting("code")
            .contains(CommonValidationErrorCode.FIELD_TOO_LONG.name)
        
        assertThat(errors[0].params["max"])
    }
    
    @Test
    fun `when format is invalid, validateEmail should return invalid format error`() {
        val email = "invalid email"
        
        val errors = fieldValidator.validateEmail(email)
        
        assertThat(errors)
            .extracting("code")
            .contains(EmailErrorCode.INVALID_FORMAT.name)
    }
    
    @Test
    fun `when valid, validatePassword should return empty list`() {
        val password = "Password123!"
        
        val errors = fieldValidator.validatePassword(password)
        
        assertThat(errors).isEmpty()
    }
    
    @Test
    fun `when password is empty, validatePassword should return empty field error`() {
        val password = ""
        
        val errors = fieldValidator.validatePassword(password)
        
        assertThat(errors)
            .extracting("code")
            .contains(CommonValidationErrorCode.FIELD_CANNOT_BE_EMPTY.name)
    }
    
    @Test
    fun `when password too short, validatePassword should return too short error`() {
        val password = "aaa"
        
        val errors = fieldValidator.validatePassword(password)
        
        assertThat(errors)
            .extracting("code")
            .contains(CommonValidationErrorCode.FIELD_TOO_SHORT.name)
        
        assertThat(errors[0].params["max"])
    }
    
    @Test
    fun `when lowercase char does not exists, validatePassword should return needs lowercase error`() {
        val password = "PASSWORD123!"
        
        val errors = fieldValidator.validatePassword(password)
        
        assertThat(errors)
            .extracting("code")
            .contains(PasswordErrorCode.NEEDS_LOWERCASE.name)
    }
    
    @Test
    fun `when uppercase char does not exists, validatePassword should return needs uppercase error`() {
        val password = "password123!"
        
        val errors = fieldValidator.validatePassword(password)
        
        assertThat(errors)
            .extracting("code")
            .contains(PasswordErrorCode.NEEDS_UPPERCASE.name)
    }
    
    @Test
    fun `when digit does not exists, validatePassword should return needs digit error`() {
        val password = "Password!"
        
        val errors = fieldValidator.validatePassword(password)
        
        assertThat(errors)
            .extracting("code")
            .contains(PasswordErrorCode.NEEDS_DIGIT.name)
    }
    
    @Test
    fun `when special char does not exists, validatePassword should return needs special char error`() {
        val password = "Password123"
        
        val errors = fieldValidator.validatePassword(password)
        
        assertThat(errors)
            .extracting("code")
            .contains(PasswordErrorCode.NEEDS_SPECIAL_CHAR.name)
    }
    
    @Test
    fun `when forbidden char exists, validatePassword should return forbidden char error`() {
        val password = "Password 123!"
        
        val errors = fieldValidator.validatePassword(password)
        
        assertThat(errors)
            .extracting("code")
            .contains(PasswordErrorCode.FORBIDDEN_CHAR.name)
        
        assertNotNull(errors[0].params["forbidden"])
    }
    
    @Test
    fun `when valid, validateRefreshToken should return empty list`() {
        val refreshToken = "token"
        
        val errors = fieldValidator.validateRefreshToken(refreshToken)
        
        assertThat(errors).isEmpty()
    }
    
    @Test
    fun `when refreshToken is blank, validateRefreshToken should return empty field error`() {
        val refreshToken = "  "
        
        val errors = fieldValidator.validateRefreshToken(refreshToken)
        
        assertThat(errors)
            .extracting("code")
            .contains(CommonValidationErrorCode.FIELD_CANNOT_BE_EMPTY.name)
    }
}