package ru.jerael.booktracker.backend.data.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.factory.author.AuthorDomainFactory
import ru.jerael.booktracker.backend.factory.author.AuthorEntityFactory

class AuthorDataMapperTest {
    private val mapper = AuthorDataMapper()
    
    @Test
    fun `toEntity should map Author to AuthorEntity`() {
        val author = AuthorDomainFactory.createAuthor()
        
        val entity = mapper.toEntity(author)
        
        with(entity) {
            assertEquals(author.id, id)
            assertEquals(author.fullName, fullName)
        }
    }
    
    @Test
    fun `toDomain should map AuthorEntity to Author`() {
        val entity = AuthorEntityFactory.createAuthorEntity()
        
        val author = mapper.toDomain(entity)
        
        with(author) {
            assertEquals(entity.id, id)
            assertEquals(entity.fullName, fullName)
        }
    }
}