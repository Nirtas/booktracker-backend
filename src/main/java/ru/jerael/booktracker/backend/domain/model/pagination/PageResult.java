package ru.jerael.booktracker.backend.domain.model.pagination;

import java.util.List;
import java.util.function.Function;

public record PageResult<T>(
    List<T> content,
    int size,
    int number,
    long totalElements,
    int totalPages
) {
    public <R> PageResult<R> map(Function<T, R> mapper) {
        List<R> mappedContent = content.stream().map(mapper).toList();
        return new PageResult<>(mappedContent, size, number, totalElements, totalPages);
    }
}
