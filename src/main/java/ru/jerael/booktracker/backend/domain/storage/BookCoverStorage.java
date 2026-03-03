package ru.jerael.booktracker.backend.domain.storage;

import ru.jerael.booktracker.backend.domain.model.image.ImageFile;

public interface BookCoverStorage {
    void save(ImageFile data);

    void delete(String fileName);
}
