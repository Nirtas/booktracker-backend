package ru.jerael.booktracker.backend.domain.model.pagination;

public record PageQuery(
    int page,
    int size
) {
    public PageQuery {
        if (page < 0) page = 0;
        if (size < 1) size = 10;
        if (size > 100) size = 100;
    }
}
