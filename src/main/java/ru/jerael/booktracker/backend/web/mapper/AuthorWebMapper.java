package ru.jerael.booktracker.backend.web.mapper;

import ru.jerael.booktracker.backend.domain.model.author.Author;
import ru.jerael.booktracker.backend.web.annotation.WebMapper;
import ru.jerael.booktracker.backend.web.dto.author.AuthorResponse;

@WebMapper
public class AuthorWebMapper {
    public AuthorResponse toResponse(Author author) {
        if (author == null) return null;

        return new AuthorResponse(
            author.id(),
            author.fullName()
        );
    }
}
