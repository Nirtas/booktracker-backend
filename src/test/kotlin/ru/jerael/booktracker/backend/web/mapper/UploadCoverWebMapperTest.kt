package ru.jerael.booktracker.backend.web.mapper

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.web.multipart.MultipartFile
import ru.jerael.booktracker.backend.domain.exception.InternalException
import ru.jerael.booktracker.backend.factory.file.FileFactory
import ru.jerael.booktracker.backend.web.exception.code.WebErrorCode
import java.io.IOException

class UploadCoverWebMapperTest {
    private val mapper = UploadCoverWebMapper()
    
    @Test
    fun `toDomain should map MultipartFile to UploadCover`() {
        val file = FileFactory.createMockMultipartFile()
        
        val data = mapper.toDomain(file)
        
        with(data) {
            assertEquals(file.contentType, contentType)
            assertEquals(file.size, size)
            assertTrue(file.bytes.contentEquals(content.readAllBytes()))
        }
    }
    
    @Test
    fun `toDomain should throw InternalException when IOException occurs`() {
        val fileName = "image.jpg"
        val file = mockk<MultipartFile>()
        every { file.inputStream } throws IOException("Error")
        every { file.originalFilename } returns fileName
        
        val exception = assertThrows(InternalException::class.java) { mapper.toDomain(file) }
        
        with(exception) {
            assertEquals(WebErrorCode.INTERNAL_SERVER_ERROR, errorCode)
            assertTrue(message?.contains(fileName) == true)
        }
    }
}