package ru.jerael.booktracker.backend.web.validator

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import ru.jerael.booktracker.backend.domain.exception.ValidationException
import ru.jerael.booktracker.backend.factory.file.FileFactory

class FileValidatorTest {
    private val validator = FileValidator()
    
    @Test
    fun `validate should pass validation for valid file`() {
        val file = FileFactory.createMockMultipartFile()
        
        assertDoesNotThrow { validator.validate(file, "cover") }
    }
    
    @Test
    fun `when file is null, validate should throw ValidationException`() {
        val file = null
        
        val exception = assertThrows(ValidationException::class.java) {
            validator.validate(file, "cover")
        }
        
        assertThat(exception.errors).anyMatch { it.field.equals("cover") }
    }
    
    @Test
    fun `when file is empty, validate should throw ValidationException`() {
        val file = FileFactory.createMockMultipartFile(content = ByteArray(0))
        
        val exception = assertThrows(ValidationException::class.java) {
            validator.validate(file, "cover")
        }
        
        assertThat(exception.errors).anyMatch { it.field.equals("cover") }
    }
    
    @Test
    fun `when fileName is empty, validate should throw ValidationException`() {
        val file = FileFactory.createMockMultipartFile(originalFileName = "")
        
        val exception = assertThrows(ValidationException::class.java) {
            validator.validate(file, "cover")
        }
        
        assertThat(exception.errors).anyMatch { it.field.equals("cover") }
    }
    
    @Test
    fun `when fileName is blank, validate should throw ValidationException`() {
        val file = FileFactory.createMockMultipartFile(originalFileName = "  ")
        
        val exception = assertThrows(ValidationException::class.java) {
            validator.validate(file, "cover")
        }
        
        assertThat(exception.errors).anyMatch { it.field.equals("cover") }
    }
}