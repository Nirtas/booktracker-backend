package ru.jerael.booktracker.backend.web.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.factory.publisher.PublisherDomainFactory

class PublisherWebMapperTest {
    private val mapper = PublisherWebMapper()
    
    @Test
    fun `toResponse should map Publisher to PublisherResponse`() {
        val publisher = PublisherDomainFactory.createPublisher()
        
        val response = mapper.toResponse(publisher)
        
        with(response) {
            assertEquals(publisher.id, id)
            assertEquals(publisher.name, name)
        }
    }
}