package ru.jerael.booktracker.backend.domain.repository;

import ru.jerael.booktracker.backend.domain.model.publisher.Publisher;
import java.util.Optional;

public interface PublisherRepository {
    Optional<Publisher> findByName(String name);

    Publisher save(Publisher publisher);
}
