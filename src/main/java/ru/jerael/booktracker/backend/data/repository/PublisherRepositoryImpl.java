package ru.jerael.booktracker.backend.data.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.jerael.booktracker.backend.data.db.entity.PublisherEntity;
import ru.jerael.booktracker.backend.data.db.repository.JpaPublisherRepository;
import ru.jerael.booktracker.backend.data.mapper.PublisherDataMapper;
import ru.jerael.booktracker.backend.domain.model.pagination.PageQuery;
import ru.jerael.booktracker.backend.domain.model.pagination.PageResult;
import ru.jerael.booktracker.backend.domain.model.publisher.Publisher;
import ru.jerael.booktracker.backend.domain.repository.PublisherRepository;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PublisherRepositoryImpl implements PublisherRepository {
    private final JpaPublisherRepository jpaPublisherRepository;
    private final PublisherDataMapper publisherDataMapper;

    @Override
    public Optional<Publisher> findByName(String name) {
        return jpaPublisherRepository.findByNameIgnoreCase(name).map(publisherDataMapper::toDomain);
    }

    @Override
    public Publisher save(Publisher publisher) {
        PublisherEntity entity = publisherDataMapper.toEntity(publisher);
        PublisherEntity savedEntity = jpaPublisherRepository.save(entity);
        return publisherDataMapper.toDomain(savedEntity);
    }

    @Override
    public PageResult<Publisher> searchByName(PageQuery pageQuery, String query) {
        int page = pageQuery != null ? pageQuery.page() : 0;
        int size = pageQuery != null ? pageQuery.size() : 5;
        Pageable pageable = PageRequest.of(page, size);

        List<PublisherEntity> entities = jpaPublisherRepository.searchBySimilarity(query, pageable);
        return new PageResult<>(
            entities.stream().map(publisherDataMapper::toDomain).toList(),
            size,
            page,
            entities.size(),
            1
        );
    }
}
