package ru.jerael.booktracker.backend.factory.file

import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile

object FileFactory {
    fun createMockMultipartFile(
        name: String = "cover",
        originalFileName: String = "image.jpg",
        contentType: String = MediaType.IMAGE_JPEG_VALUE,
        content: ByteArray = "content".toByteArray()
    ): MockMultipartFile {
        return MockMultipartFile(name, originalFileName, contentType, content)
    }
}