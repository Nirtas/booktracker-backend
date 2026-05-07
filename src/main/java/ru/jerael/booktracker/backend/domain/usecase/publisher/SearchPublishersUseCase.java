package ru.jerael.booktracker.backend.domain.usecase.publisher;

import ru.jerael.booktracker.backend.domain.model.pagination.PageQuery;
import ru.jerael.booktracker.backend.domain.model.pagination.PageResult;
import ru.jerael.booktracker.backend.domain.model.publisher.Publisher;

public interface SearchPublishersUseCase {
    PageResult<Publisher> execute(PageQuery pageQuery, String query);
}
