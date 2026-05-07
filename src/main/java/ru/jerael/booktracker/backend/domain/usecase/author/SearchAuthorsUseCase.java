package ru.jerael.booktracker.backend.domain.usecase.author;

import ru.jerael.booktracker.backend.domain.model.author.Author;
import ru.jerael.booktracker.backend.domain.model.pagination.PageQuery;
import ru.jerael.booktracker.backend.domain.model.pagination.PageResult;

public interface SearchAuthorsUseCase {
    PageResult<Author> execute(PageQuery pageQuery, String query);
}
