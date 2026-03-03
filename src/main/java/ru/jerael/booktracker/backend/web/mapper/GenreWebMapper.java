package ru.jerael.booktracker.backend.web.mapper;

import org.springframework.stereotype.Component;
import ru.jerael.booktracker.backend.web.dto.genre.GenreResponse;
import ru.jerael.booktracker.backend.domain.model.Genre;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
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
