package ru.jerael.booktracker.backend.application.validator

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.domain.constant.AuthorRules
import ru.jerael.booktracker.backend.domain.constant.BookRules
import ru.jerael.booktracker.backend.domain.constant.LanguageRules
import ru.jerael.booktracker.backend.domain.constant.PublisherRules
import ru.jerael.booktracker.backend.domain.exception.ValidationException
import ru.jerael.booktracker.backend.domain.exception.code.CommonValidationErrorCode
import ru.jerael.booktracker.backend.factory.book.BookDomainFactory

class BookValidatorImplTest {
    private val validator = BookValidatorImpl()
    
    @Test
    fun `when valid, validateUpdate should not throw Exception`() {
        val data = BookDomainFactory.createBookDetailsUpdateFilled()
        
        assertDoesNotThrow { validator.validateUpdate(data) }
    }
    
    @Test
    fun `when valid, validateCreation should not throw Exception`() {
        val data = BookDomainFactory.createBookCreation()
        
        assertDoesNotThrow { validator.validateCreation(data) }
    }
    
    @Test
    fun `when title is blank, validateCreation should throw ValidationException`() {
        val data = BookDomainFactory.createBookCreation(title = "  ")
        
        val exception = assertThrows(ValidationException::class.java) { validator.validateCreation(data) }
        
        assertThat(exception.errors).anyMatch {
            it.field == "title" && it.code == CommonValidationErrorCode.FIELD_CANNOT_BE_EMPTY.name
        }
    }
    
    @Test
    fun `when title too long, validateCreation should throw ValidationException`() {
        val title = "a".repeat(BookRules.TITLE_MAX_LENGTH + 1)
        val data = BookDomainFactory.createBookCreation(title = title)
        
        val exception = assertThrows(ValidationException::class.java) { validator.validateCreation(data) }
        
        assertThat(exception.errors).anyMatch {
            it.field == "title" && it.code == CommonValidationErrorCode.FIELD_TOO_LONG.name
        }
    }
    
    @Test
    fun `when authorNames is empty, validateCreation should throw ValidationException`() {
        val data = BookDomainFactory.createBookCreation(authorNames = emptySet())
        
        val exception = assertThrows(ValidationException::class.java) { validator.validateCreation(data) }
        
        assertThat(exception.errors).anyMatch {
            it.field == "authorNames" && it.code == CommonValidationErrorCode.FIELD_CANNOT_BE_EMPTY.name
        }
    }
    
    @Test
    fun `when authorName is blank, validateCreation should throw ValidationException`() {
        val data = BookDomainFactory.createBookCreation(authorNames = setOf("Author A", "  "))
        
        val exception = assertThrows(ValidationException::class.java) { validator.validateCreation(data) }
        
        assertThat(exception.errors).anyMatch { it.field == "authorNames" }
    }
    
    @Test
    fun `when authorName too long, validateCreation should throw ValidationException`() {
        val name = "a".repeat(AuthorRules.AUTHOR_FULL_NAME_MAX_LENGTH + 1)
        val data = BookDomainFactory.createBookCreation(authorNames = setOf(name))
        
        val exception = assertThrows(ValidationException::class.java) { validator.validateCreation(data) }
        
        assertThat(exception.errors).anyMatch {
            it.field == "authorNames" && it.code == CommonValidationErrorCode.FIELD_TOO_LONG.name
        }
    }
    
    @Test
    fun `when description too long, validateCreation should throw ValidationException`() {
        val description = "a".repeat(BookRules.DESCRIPTION_MAX_LENGTH + 1)
        val data = BookDomainFactory.createBookCreation(description = description)
        
        val exception = assertThrows(ValidationException::class.java) { validator.validateCreation(data) }
        
        assertThat(exception.errors).anyMatch {
            it.field == "description" && it.code == CommonValidationErrorCode.FIELD_TOO_LONG.name
        }
    }
    
    @Test
    fun `when publisherName too long, validateCreation should throw ValidationException`() {
        val name = "a".repeat(PublisherRules.PUBLISHER_NAME_MAX_LENGTH + 1)
        val data = BookDomainFactory.createBookCreation(publisherName = name)
        
        val exception = assertThrows(ValidationException::class.java) { validator.validateCreation(data) }
        
        assertThat(exception.errors).anyMatch {
            it.field == "publisherName" && it.code == CommonValidationErrorCode.FIELD_TOO_LONG.name
        }
    }
    
    @Test
    fun `when languageCode length is invalid, validateCreation should throw ValidationException`() {
        val code = "a".repeat(LanguageRules.LANGUAGE_CODE_LENGTH + 1)
        val data = BookDomainFactory.createBookCreation(languageCode = code)
        
        val exception = assertThrows(ValidationException::class.java) { validator.validateCreation(data) }
        
        assertThat(exception.errors).anyMatch {
            it.field == "languageCode" && it.code == CommonValidationErrorCode.FIELD_TOO_LONG.name
        }
    }
    
    @Test
    fun `when publishedOn out of range, validateCreation should throw ValidationException`() {
        val data = BookDomainFactory.createBookCreation(publishedOn = BookRules.PUBLISHED_ON_MAX + 1)
        
        val exception = assertThrows(ValidationException::class.java) { validator.validateCreation(data) }
        
        assertThat(exception.errors).anyMatch { it.field == "publishedOn" }
    }
    
    @Test
    fun `when totalPages is zero or negative, validateCreation should throw ValidationException`() {
        val data = BookDomainFactory.createBookCreation(totalPages = 0)
        
        val exception = assertThrows(ValidationException::class.java) { validator.validateCreation(data) }
        
        assertThat(exception.errors).anyMatch { it.field == "totalPages" }
    }
    
    @Test
    fun `when isbn10 length is invalid, validateCreation should throw ValidationException`() {
        val data = BookDomainFactory.createBookCreation(isbn10 = "123")
        
        val exception = assertThrows(ValidationException::class.java) { validator.validateCreation(data) }
        
        assertThat(exception.errors).anyMatch {
            it.field == "isbn10" && it.code == CommonValidationErrorCode.INVALID_LENGTH.name
        }
    }
    
    @Test
    fun `when isbn13 length is invalid, validateCreation should throw ValidationException`() {
        val data = BookDomainFactory.createBookCreation(isbn13 = "123")
        
        val exception = assertThrows(ValidationException::class.java) { validator.validateCreation(data) }
        
        assertThat(exception.errors).anyMatch {
            it.field == "isbn13" && it.code == CommonValidationErrorCode.INVALID_LENGTH.name
        }
    }
}