package ru.jerael.booktracker.backend.domain.model.pagination;

public record PageQuery(
    int page,
    int size,
    String sortBy,
    SortDirection direction
) {
    public PageQuery {
        if (page < 0) page = 0;
        if (size < 1) size = 10;
        if (size > 100) size = 100;
        if (sortBy == null || sortBy.isBlank()) sortBy = "createdAt";
        if (direction == null) direction = SortDirection.DESC;
    }
}
