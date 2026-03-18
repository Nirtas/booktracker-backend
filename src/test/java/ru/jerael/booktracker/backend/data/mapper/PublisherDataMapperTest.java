package ru.jerael.booktracker.backend.data.mapper;

import org.junit.jupiter.api.Test;
import ru.jerael.booktracker.backend.data.db.entity.PublisherEntity;
import ru.jerael.booktracker.backend.domain.model.publisher.Publisher;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PublisherDataMapperTest {
    private final PublisherDataMapper publisherDataMapper = new PublisherDataMapper();

    private final UUID id = UUID.fromString("a95f2c31-acd2-4413-83ea-c775d6cda257");
    private final String name = "Publisher Name";

    @Test
    void toEntity() {
        Publisher publisher = new Publisher(id, name);

        PublisherEntity entity = publisherDataMapper.toEntity(publisher);

        assertEquals(id, entity.getId());
        assertEquals(name, entity.getName());
    }

    @Test
    void toDomain() {
        PublisherEntity entity = new PublisherEntity();
        entity.setId(id);
        entity.setName(name);

        Publisher publisher = publisherDataMapper.toDomain(entity);

        assertEquals(id, publisher.id());
        assertEquals(name, publisher.name());
    }
}