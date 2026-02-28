package ru.jerael.booktracker.backend.domain.storage;

import java.io.InputStream;
import java.util.UUID;

public interface BookCoverStorage {
    String save(UUID bookId, String contentType, InputStream content);

    void delete(String fileName);
}
