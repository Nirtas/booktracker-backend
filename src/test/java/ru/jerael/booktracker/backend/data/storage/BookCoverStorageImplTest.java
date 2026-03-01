package ru.jerael.booktracker.backend.data.storage;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import ru.jerael.booktracker.backend.domain.exception.InternalException;
import ru.jerael.booktracker.backend.domain.model.book.UploadCover;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@Deprecated
@Disabled
@SpringBootTest
class BookCoverStorageImplTest {

    @Autowired
    private BookCoverStorageImpl bookCoverStorage;

    @TempDir
    static Path tempDir;

    private static Path coversPath;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        coversPath = tempDir.resolve("covers");
        registry.add("app.storage.covers-path", () -> coversPath.toString());
    }

    private final UUID id = UUID.fromString("3a60a981-acad-4b0b-b9d0-a6d24ffb6b94");
    private final String extension = "jpg";
    private final String content = "content";
    private final InputStream inputStream = new ByteArrayInputStream(content.getBytes());
    private final long inputStreamSize = content.getBytes().length;

    @AfterAll
    static void clean() throws IOException {
        if (Files.exists(tempDir)) {
            Files.walk(tempDir)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
        }
        Files.createDirectories(tempDir);
    }

    @Test
    void init_ShouldCreateDirectoryOnInitialization() {
        assertTrue(Files.exists(coversPath));
        assertTrue(Files.isDirectory(coversPath));
    }

    @Test
    void save_ShouldSaveCover() throws IOException {
        UploadCover data = new UploadCover("image/jpeg", inputStream, inputStreamSize);
        String savedFileName = bookCoverStorage.save(id, data);

        assertEquals(String.format("%s.%s", id, extension), savedFileName);

        Path expected = coversPath.resolve(savedFileName);
        assertTrue(Files.exists(expected));
        assertTrue(Files.isRegularFile(expected));
        assertEquals(content, Files.readString(expected));
    }

    @Test
    void save_ShouldReplaceExistingCover() throws IOException {
        String newContent = "new content";
        InputStream newInputStream = new ByteArrayInputStream(newContent.getBytes());
        UploadCover data = new UploadCover("image/jpeg", newInputStream, newContent.getBytes().length);

        String savedFileName = bookCoverStorage.save(id, data);

        assertEquals(String.format("%s.%s", id, extension), savedFileName);

        Path expected = coversPath.resolve(savedFileName);
        assertTrue(Files.exists(expected));
        assertTrue(Files.isRegularFile(expected));
        assertEquals(newContent, Files.readString(expected));
    }

    @Test
    void delete_ShouldDeleteCover() {
        String newContent = "new content";
        InputStream newInputStream = new ByteArrayInputStream(newContent.getBytes());
        UploadCover data = new UploadCover("image/jpeg", newInputStream, newContent.getBytes().length);

        String savedFileName = bookCoverStorage.save(id, data);

        Path path = coversPath.resolve(savedFileName);
        assertTrue(Files.exists(path));

        bookCoverStorage.delete(savedFileName);

        assertFalse(Files.exists(path));
    }

    @Test
    void delete_WhenFileDoesNotExist_ShouldNotThrowException() {
        assertDoesNotThrow(() -> bookCoverStorage.delete("cover.jpg"));
    }

    @Test
    void delete_WhenIOExceptionOccurs_ShouldThrowStorageException() throws IOException {
        String directory = "directory";
        Files.createDirectory(coversPath.resolve(directory));
        Files.createFile(coversPath.resolve(directory).resolve("cover.jpg"));

        assertThrows(InternalException.class, () -> bookCoverStorage.delete(directory));
    }
}