package ru.jerael.booktracker.backend.application.usecase.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import ru.jerael.booktracker.backend.application.annotation.UseCase;
import ru.jerael.booktracker.backend.domain.model.pagination.PageQuery;
import ru.jerael.booktracker.backend.domain.model.pagination.PageResult;
import ru.jerael.booktracker.backend.domain.model.publisher.Publisher;
import ru.jerael.booktracker.backend.domain.repository.PublisherRepository;
import ru.jerael.booktracker.backend.domain.usecase.publisher.SearchPublishersUseCase;
import java.util.List;

@UseCase
@RequiredArgsConstructor
public class SearchPublishersUseCaseImpl implements SearchPublishersUseCase {
    private final PublisherRepository publisherRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResult<Publisher> execute(PageQuery pageQuery, String query) {
        String cleanedQuery = query != null ? query.trim() : null;
        if (cleanedQuery == null || cleanedQuery.length() < 3) {
            return new PageResult<>(List.of(), 0, pageQuery.page(), 0, 0);
        }
        return publisherRepository.searchByName(pageQuery, query);
    }
}
