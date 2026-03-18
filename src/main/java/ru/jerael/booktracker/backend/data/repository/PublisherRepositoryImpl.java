package ru.jerael.booktracker.backend.data.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.jerael.booktracker.backend.data.db.entity.PublisherEntity;
import ru.jerael.booktracker.backend.data.db.repository.JpaPublisherRepository;
import ru.jerael.booktracker.backend.data.mapper.PublisherDataMapper;
import ru.jerael.booktracker.backend.domain.model.publisher.Publisher;
import ru.jerael.booktracker.backend.domain.repository.PublisherRepository;
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
}
