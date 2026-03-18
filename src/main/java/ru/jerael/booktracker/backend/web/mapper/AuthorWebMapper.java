package ru.jerael.booktracker.backend.web.mapper;

import org.springframework.stereotype.Component;
import ru.jerael.booktracker.backend.domain.model.author.Author;
import ru.jerael.booktracker.backend.web.dto.author.AuthorResponse;

@Component
public class AuthorWebMapper {
    public AuthorResponse toResponse(Author author) {
        if (author == null) return null;

        return new AuthorResponse(
            author.id(),
            author.fullName()
        );
    }
}
