package ru.jerael.booktracker.backend.data.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.factory.publisher.PublisherDomainFactory
import ru.jerael.booktracker.backend.factory.publisher.PublisherEntityFactory

class PublisherDataMapperTest {
    private val mapper = PublisherDataMapper()
    
    @Test
    fun `toEntity should map Publisher to PublisherEntity`() {
        val publisher = PublisherDomainFactory.createPublisher()
        
        val entity = mapper.toEntity(publisher)
        
        with(entity) {
            assertEquals(publisher.id, id)
            assertEquals(publisher.name, name)
        }
    }
    
    @Test
    fun `toDomain should map PublisherEntity to Publisher`() {
        val entity = PublisherEntityFactory.createPublisherEntity()
        
        val publisher = mapper.toDomain(entity)
        
        with(publisher) {
            assertEquals(entity.id, id)
            assertEquals(entity.name, name)
        }
    }
}