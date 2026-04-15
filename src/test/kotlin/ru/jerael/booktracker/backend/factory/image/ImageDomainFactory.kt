package ru.jerael.booktracker.backend.factory.image

import org.springframework.http.MediaType
import ru.jerael.booktracker.backend.domain.model.image.ImageFile
import ru.jerael.booktracker.backend.domain.model.image.ImageFormat
import ru.jerael.booktracker.backend.domain.model.image.ProcessedImage
import java.io.InputStream

object ImageDomainFactory {
    fun createImageFile(
        fileName: String = "image.jpg",
        contentType: String = MediaType.IMAGE_JPEG_VALUE,
        content: InputStream = "content".toByteArray().inputStream(),
        size: Long = "content".length.toLong()
    ): ImageFile {
        return ImageFile(fileName, contentType, content, size)
    }
    
    fun createProcessedImage(
        contentType: String = MediaType.IMAGE_JPEG_VALUE,
        extension: String = ImageFormat.JPG.extension,
        content: InputStream = "content".toByteArray().inputStream(),
        size: Long = "content".length.toLong()
    ): ProcessedImage {
        return ProcessedImage(contentType, extension, content, size)
    }
}
