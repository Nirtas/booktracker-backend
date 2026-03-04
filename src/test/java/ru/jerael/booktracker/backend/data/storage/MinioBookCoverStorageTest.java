package ru.jerael.booktracker.backend.data.storage;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.jerael.booktracker.backend.domain.model.image.ImageFile;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
class MinioBookCoverStorageTest {

    private static final String USER = "testuser";
    private static final String PASSWORD = "testpassword";
    private static final String COVERS_BUCKET = "covers";

    @Container
    private static final GenericContainer<?> minioContainer =
        new GenericContainer<>(DockerImageName.parse("minio/minio:RELEASE.2025-09-07T16-13-09Z"))
            .withEnv("MINIO_ROOT_USER", USER)
            .withEnv("MINIO_ROOT_PASSWORD", PASSWORD)
            .withCommand("server /data")
            .withExposedPorts(9000);

    @Autowired
    private MinioBookCoverStorage bookCoverStorage;

    @Autowired
    private MinioClient minioClient;

    @DynamicPropertySource
    private static void configureMinioProperties(DynamicPropertyRegistry registry) {
        registry.add("app.minio.url",
            () -> "http://" + minioContainer.getHost() + ":" + minioContainer.getMappedPort(9000));
    }

    private final UUID id = UUID.fromString("3a60a981-acad-4b0b-b9d0-a6d24ffb6b94");
    private final String content = "content";

    @Test
    void init_ShouldCreateBucketOnInitialization() throws Exception {
        boolean exists = minioClient.bucketExists(
            BucketExistsArgs.builder()
                .bucket(COVERS_BUCKET)
                .build()
        );
        assertTrue(exists);
    }

    @Test
    void save_ShouldSaveCover() throws Exception {
        InputStream inputStream = new ByteArrayInputStream(content.getBytes());
        long inputStreamSize = content.getBytes().length;
        String coverFileName = id + ".jpg";
        ImageFile data = new ImageFile(coverFileName, "image/jpeg", inputStream, inputStreamSize);

        bookCoverStorage.save(data);

        assertEquals(id + ".jpg", coverFileName);
        try (InputStream stream = minioClient.getObject(
            GetObjectArgs.builder()
                .bucket(COVERS_BUCKET)
                .object(coverFileName)
                .build()
        )) {
            String streamContent = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            assertEquals(content, streamContent);
        }
    }

    @Test
    void delete_ShouldDeleteCover() throws Exception {
        InputStream inputStream = new ByteArrayInputStream(content.getBytes());
        long inputStreamSize = content.getBytes().length;
        String coverFileName = "cover.jpg";
        ImageFile data = new ImageFile(coverFileName, "image/jpeg", inputStream, inputStreamSize);
        bookCoverStorage.save(data);
        minioClient.statObject(
            StatObjectArgs.builder()
                .bucket(COVERS_BUCKET)
                .object(coverFileName)
                .build()
        );

        bookCoverStorage.delete(coverFileName);

        String code = assertThrows(
            ErrorResponseException.class,
            () -> minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(COVERS_BUCKET)
                    .object(coverFileName)
                    .build()
            )
        ).errorResponse().code();
        assertEquals("NoSuchKey", code);
    }

    @Test
    void download_ShouldReturnValidImageFile() throws Exception {
        InputStream inputStream = new ByteArrayInputStream(content.getBytes());
        long inputStreamSize = content.getBytes().length;
        String coverFileName = "cover.jpg";
        ImageFile data = new ImageFile(coverFileName, "image/jpeg", inputStream, inputStreamSize);
        bookCoverStorage.save(data);

        ImageFile downloadFile = bookCoverStorage.download(coverFileName);

        try {
            assertNotNull(downloadFile);
            assertEquals(coverFileName, downloadFile.fileName());
            assertEquals("image/jpeg", downloadFile.contentType());
            assertEquals(inputStreamSize, downloadFile.size());

            byte[] downloadedBytes = downloadFile.content().readAllBytes();
            assertArrayEquals(content.getBytes(), downloadedBytes);
        } finally {
            downloadFile.content().close();
        }
    }
}