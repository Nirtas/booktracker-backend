package ru.jerael.booktracker.backend.domain.model.pagination;

import ru.jerael.booktracker.backend.domain.constant.PaginationRules;

public record PageQuery(
    int page,
    int size,
    String sortBy,
    SortDirection direction
) {
    public PageQuery {
        if (page < 0) page = 0;
        if (size < 1) size = PaginationRules.DEFAULT_PAGE_SIZE;
        if (size > 100) size = PaginationRules.MAX_PAGE_SIZE;
        if (sortBy == null || sortBy.isBlank()) sortBy = PaginationRules.DEFAULT_SORT_FIELD;
        if (direction == null) direction = PaginationRules.DEFAULT_SORT_DIRECTION;
    }
}
