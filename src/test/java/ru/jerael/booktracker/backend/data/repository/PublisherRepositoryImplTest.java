package ru.jerael.booktracker.backend.data.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.jerael.booktracker.backend.data.db.entity.PublisherEntity;
import ru.jerael.booktracker.backend.data.db.repository.JpaPublisherRepository;
import ru.jerael.booktracker.backend.data.mapper.PublisherDataMapper;
import ru.jerael.booktracker.backend.domain.model.publisher.Publisher;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({PublisherRepositoryImpl.class, PublisherDataMapper.class})
class PublisherRepositoryImplTest {

    @Autowired
    private PublisherRepositoryImpl publisherRepository;

    @Autowired
    private JpaPublisherRepository jpaPublisherRepository;

    private final String name = "Publisher Name";

    @Test
    void findByName_WhenExists_ShouldReturnPublisher() {
        PublisherEntity entity = new PublisherEntity();
        entity.setName(name);
        PublisherEntity savedEntity = jpaPublisherRepository.save(entity);

        Optional<Publisher> result = publisherRepository.findByName("publisher name");

        assertTrue(result.isPresent());
        assertEquals(savedEntity.getId(), result.get().id());
        assertEquals(name, result.get().name());
    }

    @Test
    void save_WhenIdIsNull_ShouldInsertNewPublisher() {
        Publisher publisher = new Publisher(null, name);

        Publisher result = publisherRepository.save(publisher);

        assertNotNull(result);
        assertEquals(name, result.name());
    }

    @Test
    void save_WhenIdIsPresent_ShouldUpdateExistingPublisher() {
        Publisher publisher = new Publisher(null, name);

        UUID savedId = publisherRepository.save(publisher).id();

        Publisher newPublisher = new Publisher(savedId, "New Publisher");

        Publisher result = publisherRepository.save(newPublisher);

        assertEquals(savedId, result.id());
        assertEquals("New Publisher", result.name());
    }
}