package ru.jerael.booktracker.backend.web.mapper;

import ru.jerael.booktracker.backend.domain.model.genre.Genre;
import ru.jerael.booktracker.backend.web.annotation.WebMapper;
import ru.jerael.booktracker.backend.web.dto.genre.GenreResponse;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@WebMapper
public class GenreWebMapper {
    public GenreResponse toResponse(Genre genre) {
        if (genre == null) return null;

        return new GenreResponse(genre.id(), genre.name());
    }

    public Set<GenreResponse> toResponses(Set<Genre> genres) {
        if (genres == null) return Set.of();

        return genres.stream().map(this::toResponse).collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
