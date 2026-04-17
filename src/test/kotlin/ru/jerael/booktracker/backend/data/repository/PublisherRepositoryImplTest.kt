package ru.jerael.booktracker.backend.data.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.context.annotation.Import
import ru.jerael.booktracker.backend.data.db.repository.JpaPublisherRepository
import ru.jerael.booktracker.backend.data.mapper.PublisherDataMapper
import ru.jerael.booktracker.backend.factory.publisher.PublisherDomainFactory
import ru.jerael.booktracker.backend.factory.publisher.PublisherEntityFactory

@DataJpaTest
@Import(PublisherRepositoryImpl::class, PublisherDataMapper::class)
class PublisherRepositoryImplTest {
    
    @Autowired
    private lateinit var jpaPublisherRepository: JpaPublisherRepository
    
    @Autowired
    private lateinit var publisherRepository: PublisherRepositoryImpl
    
    @Test
    fun `when publisher exists, findByName should return publisher`() {
        val publisher = jpaPublisherRepository.save(PublisherEntityFactory.createPublisherEntity())
        
        val result = publisherRepository.findByName(publisher.name)
        
        with(result.get()) {
            assertEquals(publisher.id, id)
            assertEquals(publisher.name, name)
        }
    }
    
    @Test
    fun `when id is null, save should insert new publisher`() {
        val publisher = PublisherDomainFactory.createPublisher(id = null)
        
        val result = publisherRepository.save(publisher)
        
        assertNotNull(result.id)
        assertEquals(publisher.name, result.name)
    }
    
    @Test
    fun `when id is present, save should update existing publisher`() {
        val savedPublisher = jpaPublisherRepository.save(PublisherEntityFactory.createPublisherEntity())
        val updatedPublisher = PublisherDomainFactory.createPublisher(id = savedPublisher.id, name = "New Publisher")
        
        val result = publisherRepository.save(updatedPublisher)
        
        assertEquals(savedPublisher.id, result.id)
        assertEquals(updatedPublisher.name, result.name)
    }
}