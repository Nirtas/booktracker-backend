package ru.jerael.booktracker.backend.data.storage;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.jerael.booktracker.backend.data.exception.factory.StorageExceptionFactory;
import ru.jerael.booktracker.backend.domain.constants.BookRules;
import ru.jerael.booktracker.backend.domain.storage.BookCoverStorage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
public class BookCoverStorageImpl implements BookCoverStorage {
    private final Path coversPath;

    public BookCoverStorageImpl(@Value("${app.storage.covers-path}") String coverPath) {
        this.coversPath = Paths.get(coverPath);
    }

    @PostConstruct
    private void init() {
        try {
            if (!Files.exists(coversPath)) {
                Files.createDirectories(coversPath);
            }
        } catch (IOException e) {
            throw StorageExceptionFactory.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public String save(UUID bookId, String contentType, InputStream content) {
        try {
            String extension = BookRules.MIME_TO_EXTENSION.get(contentType);
            String fileName = String.format("%s.%s", bookId, extension);
            Path destination = coversPath.resolve(fileName);
            Files.copy(content, destination, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException e) {
            throw StorageExceptionFactory.error(e.getLocalizedMessage(), e);
        }
    }
}
