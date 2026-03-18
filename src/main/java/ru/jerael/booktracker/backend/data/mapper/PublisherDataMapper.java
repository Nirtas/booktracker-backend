package ru.jerael.booktracker.backend.data.mapper;

import org.springframework.stereotype.Component;
import ru.jerael.booktracker.backend.data.db.entity.PublisherEntity;
import ru.jerael.booktracker.backend.domain.model.publisher.Publisher;

@Component
public class PublisherDataMapper {
    public PublisherEntity toEntity(Publisher publisher) {
        if (publisher == null) return null;

        PublisherEntity entity = new PublisherEntity();
        entity.setId(publisher.id());
        entity.setName(publisher.name());
        return entity;
    }

    public Publisher toDomain(PublisherEntity entity) {
        if (entity == null) return null;

        return new Publisher(
            entity.getId(),
            entity.getName()
        );
    }
}
