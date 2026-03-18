package ru.jerael.booktracker.backend.web.mapper;

import org.springframework.stereotype.Component;
import ru.jerael.booktracker.backend.domain.model.publisher.Publisher;
import ru.jerael.booktracker.backend.web.dto.publisher.PublisherResponse;

@Component
public class PublisherWebMapper {
    public PublisherResponse toResponse(Publisher publisher) {
        if (publisher == null) return null;

        return new PublisherResponse(
            publisher.id(),
            publisher.name()
        );
    }
}
