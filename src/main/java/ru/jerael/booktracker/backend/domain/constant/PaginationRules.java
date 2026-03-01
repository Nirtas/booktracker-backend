package ru.jerael.booktracker.backend.domain.constant;

import ru.jerael.booktracker.backend.domain.model.pagination.SortDirection;

public final class PaginationRules {
    private PaginationRules() {}

    public static final String DEFAULT_SORT_FIELD = "createdAt";
    public static final SortDirection DEFAULT_SORT_DIRECTION = SortDirection.DESC;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
}
