package ru.jerael.booktracker.backend.data.storage

import io.minio.BucketExistsArgs
import io.minio.GetObjectArgs
import io.minio.MinioClient
import io.minio.StatObjectArgs
import io.minio.errors.ErrorResponseException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import ru.jerael.booktracker.backend.config.TestcontainersConfiguration
import ru.jerael.booktracker.backend.factory.image.ImageDomainFactory
import java.util.*

@SpringBootTest
@Import(TestcontainersConfiguration::class)
class MinioBookCoverStorageTest {
    
    @Autowired
    private lateinit var minioClient: MinioClient
    
    @Autowired
    private lateinit var storage: MinioBookCoverStorage
    
    private val coversBucket = "covers"
    
    @Test
    fun `init should create bucket on initialization`() {
        val exists = minioClient.bucketExists(
            BucketExistsArgs.builder()
                .bucket(coversBucket)
                .build()
        )
        
        assertTrue(exists)
    }
    
    @Test
    fun `save should upload file to storage`() {
        val content = "content"
        val bytes = content.toByteArray()
        val inputStream = bytes.inputStream()
        val size = bytes.size.toLong()
        val coverFileName = "${UUID.randomUUID()}.jpg"
        val imageFile = ImageDomainFactory.createImageFile(
            fileName = coverFileName,
            contentType = MediaType.IMAGE_JPEG_VALUE,
            content = inputStream,
            size = size
        )
        
        storage.save(imageFile)
        
        minioClient.getObject(
            GetObjectArgs.builder()
                .bucket(coversBucket)
                .`object`(coverFileName)
                .build()
        ).use { stream ->
            val streamContent = stream.readBytes().toString(Charsets.UTF_8)
            assertEquals(content, streamContent)
        }
    }
    
    @Test
    fun `delete should delete file from minio`() {
        val content = "content"
        val bytes = content.toByteArray()
        val inputStream = bytes.inputStream()
        val size = bytes.size.toLong()
        val coverFileName = "${UUID.randomUUID()}.jpg"
        val imageFile = ImageDomainFactory.createImageFile(
            fileName = coverFileName,
            contentType = MediaType.IMAGE_JPEG_VALUE,
            content = inputStream,
            size = size
        )
        
        storage.save(imageFile)
        
        minioClient.statObject(
            StatObjectArgs.builder()
                .bucket(coversBucket)
                .`object`(coverFileName)
                .build()
        )
        
        storage.delete(coverFileName)
        
        val code = assertThrows(ErrorResponseException::class.java) {
            minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(coversBucket)
                    .`object`(coverFileName)
                    .build()
            )
        }.errorResponse().code()
        
        assertEquals("NoSuchKey", code)
    }
    
    @Test
    fun `download should return valid ImageFile`() {
        val content = "content"
        val bytes = content.toByteArray()
        val inputStream = bytes.inputStream()
        val size = bytes.size.toLong()
        val coverFileName = "${UUID.randomUUID()}.jpg"
        val imageFile = ImageDomainFactory.createImageFile(
            fileName = coverFileName,
            contentType = MediaType.IMAGE_JPEG_VALUE,
            content = inputStream,
            size = size
        )
        
        storage.save(imageFile)
        
        minioClient.statObject(
            StatObjectArgs.builder()
                .bucket(coversBucket)
                .`object`(coverFileName)
                .build()
        )
        
        val file = storage.download(coverFileName)
        
        assertNotNull(file)
        assertEquals(coverFileName, file.fileName)
        assertEquals(MediaType.IMAGE_JPEG_VALUE, file.contentType)
        assertEquals(size, file.size)
        
        file.content.use { stream ->
            assertArrayEquals(bytes, stream.readBytes())
        }
    }
}