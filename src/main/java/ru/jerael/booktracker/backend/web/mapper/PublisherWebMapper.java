package ru.jerael.booktracker.backend.web.mapper;

import ru.jerael.booktracker.backend.domain.model.publisher.Publisher;
import ru.jerael.booktracker.backend.web.annotation.WebMapper;
import ru.jerael.booktracker.backend.web.dto.publisher.PublisherResponse;

@WebMapper
public class PublisherWebMapper {
    public PublisherResponse toResponse(Publisher publisher) {
        if (publisher == null) return null;

        return new PublisherResponse(
            publisher.id(),
            publisher.name()
        );
    }
}
