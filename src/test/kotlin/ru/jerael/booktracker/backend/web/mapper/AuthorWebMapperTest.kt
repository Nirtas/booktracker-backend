package ru.jerael.booktracker.backend.web.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.factory.author.AuthorDomainFactory

class AuthorWebMapperTest {
    private val mapper = AuthorWebMapper()
    
    @Test
    fun `toResponse should map Author to AuthorResponse`() {
        val author = AuthorDomainFactory.createAuthor()
        
        val response = mapper.toResponse(author)
        
        with(response) {
            assertEquals(author.id, id)
            assertEquals(author.fullName, fullName)
        }
    }
}