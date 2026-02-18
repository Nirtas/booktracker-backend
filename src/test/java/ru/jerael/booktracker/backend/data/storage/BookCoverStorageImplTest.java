package ru.jerael.booktracker.backend.data.storage;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        String savedFileName = bookCoverStorage.save(id, extension, inputStream);

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

        String savedFileName = bookCoverStorage.save(id, extension, newInputStream);

        assertEquals(String.format("%s.%s", id, extension), savedFileName);

        Path expected = coversPath.resolve(savedFileName);
        assertTrue(Files.exists(expected));
        assertTrue(Files.isRegularFile(expected));
        assertEquals(newContent, Files.readString(expected));
    }
}