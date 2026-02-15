package ru.jerael.booktracker.backend.api.mapper;

import org.springframework.stereotype.Component;
import ru.jerael.booktracker.backend.api.dto.genre.GenreResponse;
import ru.jerael.booktracker.backend.domain.model.Genre;
import java.util.List;

@Component
public class GenreApiMapper {
    public GenreResponse toResponse(Genre genre) {
        return new GenreResponse(genre.id(), genre.name());
    }

    public List<GenreResponse> toResponses(List<Genre> genres) {
        return genres.stream().map(this::toResponse).toList();
    }
}
