package ru.jerael.booktracker.backend.web.validator

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import ru.jerael.booktracker.backend.domain.exception.ValidationException
import ru.jerael.booktracker.backend.domain.exception.code.FileValidationErrorCode
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
        
        val errorCode = assertThrows(ValidationException::class.java) {
            validator.validate(file, "cover")
        }.errorCode
        
        assertEquals(FileValidationErrorCode.EMPTY_FILE_CONTENT, errorCode)
    }
    
    @Test
    fun `when file is empty, validate should throw ValidationException`() {
        val file = FileFactory.createMockMultipartFile(content = ByteArray(0))
        
        val errorCode = assertThrows(ValidationException::class.java) {
            validator.validate(file, "cover")
        }.errorCode
        
        assertEquals(FileValidationErrorCode.EMPTY_FILE_CONTENT, errorCode)
    }
    
    @Test
    fun `when fileName is empty, validate should throw ValidationException`() {
        val file = FileFactory.createMockMultipartFile(originalFileName = "")
        
        val errorCode = assertThrows(ValidationException::class.java) {
            validator.validate(file, "cover")
        }.errorCode
        
        assertEquals(FileValidationErrorCode.EMPTY_FILE_NAME, errorCode)
    }
    
    @Test
    fun `when fileName is blank, validate should throw ValidationException`() {
        val file = FileFactory.createMockMultipartFile(originalFileName = "  ")
        
        val errorCode = assertThrows(ValidationException::class.java) {
            validator.validate(file, "cover")
        }.errorCode
        
        assertEquals(FileValidationErrorCode.EMPTY_FILE_NAME, errorCode)
    }
}