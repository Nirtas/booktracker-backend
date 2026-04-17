package ru.jerael.booktracker.backend.application.validator

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.domain.exception.ValidationException
import ru.jerael.booktracker.backend.factory.user.UserDomainFactory

class UserValidatorImplTest {
    private val fieldValidator = FieldValidatorImpl()
    private val userValidator = UserValidatorImpl(fieldValidator)
    
    @Test
    fun `when valid data provided, validateCreation should pass validation without errors`() {
        val data = UserDomainFactory.createUserCreation()
        
        assertDoesNotThrow { userValidator.validateCreation(data) }
    }
    
    @Test
    fun `when multiple invalid fields, validateCreation should throw ValidationException with all errors`() {
        val data = UserDomainFactory.createUserCreation(email = "invalid email", password = "aaa")
        
        val exception = assertThrows(ValidationException::class.java) {
            userValidator.validateCreation(data)
        }
        
        with(exception.errors) {
            assertThat(this).hasSizeGreaterThan(1)
            assertThat(this).anyMatch { it.field == "email" }
            assertThat(this).anyMatch { it.field == "password" }
        }
    }
}