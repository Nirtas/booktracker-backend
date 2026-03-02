package ru.jerael.booktracker.backend.domain.storage;

import ru.jerael.booktracker.backend.domain.model.image.SaveImage;

public interface BookCoverStorage {
    void save(SaveImage data);

    void delete(String fileName);
}
