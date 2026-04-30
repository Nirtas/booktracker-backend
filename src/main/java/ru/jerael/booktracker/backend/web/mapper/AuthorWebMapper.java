package ru.jerael.booktracker.backend.web.mapper;

import ru.jerael.booktracker.backend.domain.model.author.Author;
import ru.jerael.booktracker.backend.web.annotation.WebMapper;
import ru.jerael.booktracker.backend.web.dto.author.AuthorResponse;
import java.util.Set;
import java.util.stream.Collectors;

@WebMapper
public class AuthorWebMapper {
    public AuthorResponse toResponse(Author author) {
        if (author == null) return null;

        return new AuthorResponse(
            author.id(),
            author.fullName()
        );
    }

    public Set<AuthorResponse> toResponses(Set<Author> authors) {
        if (authors == null) return Set.of();

        return authors.stream().map(this::toResponse).collect(Collectors.toSet());
    }
}
