package ru.jerael.booktracker.backend.domain.repository;

import ru.jerael.booktracker.backend.domain.model.pagination.PageQuery;
import ru.jerael.booktracker.backend.domain.model.pagination.PageResult;
import ru.jerael.booktracker.backend.domain.model.publisher.Publisher;
import java.util.Optional;

public interface PublisherRepository {
    Optional<Publisher> findByName(String name);

    Publisher save(Publisher publisher);

    PageResult<Publisher> searchByName(PageQuery pageQuery, String query);
}
