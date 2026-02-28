package ru.jerael.booktracker.backend.domain.storage;

import ru.jerael.booktracker.backend.domain.model.book.UploadCover;
import java.util.UUID;

public interface BookCoverStorage {
    String save(UUID bookId, UploadCover data);

    void delete(String fileName);

    String getUrl(String fileName);
}
