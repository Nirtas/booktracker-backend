package ru.jerael.booktracker.backend.api.util;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.util.UUID;

@Component
public class LinkBuilder {
    public String buildCoverUrl(UUID bookId) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("api/v1/books/{id}/cover")
            .buildAndExpand(bookId)
            .toUriString();
    }
}
