package ru.jerael.booktracker.backend.data.service.image

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.data.service.image.config.ImageProperties
import ru.jerael.booktracker.backend.domain.exception.InternalException
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.imageio.ImageIO

class ThumbnailatorImageProcessorTest {
    private val imageProperties = ImageProperties()
    private val processor = ThumbnailatorImageProcessor(imageProperties)
    
    @Test
    fun `process should convert jpg to webp`() {
        val inputStream = createImage(100, 100, "jpg")
        
        val result = processor.process(inputStream)
        
        with(result) {
            assertEquals("image/webp", contentType)
            assertThat(size).isGreaterThan(0)
        }
    }
    
    @Test
    fun `process should resize large image`() {
        val inputStream = createImage(3000, 1000, "jpg")
        imageProperties.maxWidth = 1080
        
        val result = processor.process(inputStream)
        
        with(ImageIO.read(result.content)) {
            assertEquals(imageProperties.maxWidth, width)
            assertThat(height).isLessThan(1000)
        }
    }
    
    @Test
    fun `process should not upscale small image`() {
        val inputStream = createImage(500, 500, "jpg")
        imageProperties.maxWidth = 1080
        imageProperties.maxHeight = 1080
        
        val result = processor.process(inputStream)
        
        with(ImageIO.read(result.content)) {
            assertEquals(500, width)
            assertEquals(500, height)
        }
    }
    
    @Test
    fun `when data is invalid, process should throw InternalException`() {
        val inputStream = ByteArrayInputStream(byteArrayOf(0, 1, 2, 3))
        
        val exception = assertThrows(InternalException::class.java) {
            processor.process(inputStream)
        }
        
        assertThat(exception.message).contains("Image processing error")
    }
    
    private fun createImage(width: Int, height: Int, format: String): InputStream {
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        
        with(image.createGraphics()) {
            color = Color.RED
            fillRect(0, 0, width, height)
            drawString("content", 10, 10)
            dispose()
        }
        
        return ByteArrayOutputStream().use { outputStream ->
            ImageIO.write(image, format, outputStream)
            ByteArrayInputStream(outputStream.toByteArray())
        }
    }
}