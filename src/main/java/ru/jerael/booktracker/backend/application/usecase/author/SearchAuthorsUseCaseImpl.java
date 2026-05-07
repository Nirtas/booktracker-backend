package ru.jerael.booktracker.backend.application.usecase.author;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import ru.jerael.booktracker.backend.application.annotation.UseCase;
import ru.jerael.booktracker.backend.domain.model.author.Author;
import ru.jerael.booktracker.backend.domain.model.pagination.PageQuery;
import ru.jerael.booktracker.backend.domain.model.pagination.PageResult;
import ru.jerael.booktracker.backend.domain.repository.AuthorRepository;
import ru.jerael.booktracker.backend.domain.usecase.author.SearchAuthorsUseCase;
import java.util.List;

@UseCase
@RequiredArgsConstructor
public class SearchAuthorsUseCaseImpl implements SearchAuthorsUseCase {
    private final AuthorRepository authorRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResult<Author> execute(PageQuery pageQuery, String query) {
        String cleanedQuery = query != null ? query.trim() : null;
        if (cleanedQuery == null || cleanedQuery.length() < 3) {
            return new PageResult<>(List.of(), 0, pageQuery.page(), 0, 0);
        }
        return authorRepository.searchByFullName(pageQuery, query);
    }
}
