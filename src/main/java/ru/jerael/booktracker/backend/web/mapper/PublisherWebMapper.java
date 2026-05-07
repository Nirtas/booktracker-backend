package ru.jerael.booktracker.backend.web.mapper;

import ru.jerael.booktracker.backend.domain.model.pagination.PageResult;
import ru.jerael.booktracker.backend.domain.model.publisher.Publisher;
import ru.jerael.booktracker.backend.web.annotation.WebMapper;
import ru.jerael.booktracker.backend.web.dto.publisher.PublisherResponse;
import java.util.List;

@WebMapper
public class PublisherWebMapper {
    public PublisherResponse toResponse(Publisher publisher) {
        if (publisher == null) return null;

        return new PublisherResponse(
            publisher.id(),
            publisher.name()
        );
    }

    public List<PublisherResponse> toResponses(PageResult<Publisher> publishers) {
        if (publishers == null) return List.of();

        return publishers.content().stream().map(this::toResponse).toList();
    }
}
