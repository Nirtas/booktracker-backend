package ru.jerael.booktracker.backend.domain.storage;

import ru.jerael.booktracker.backend.domain.model.book.UploadCover;

public interface BookCoverStorage {
    String save(UploadCover data);
}
