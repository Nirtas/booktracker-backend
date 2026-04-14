package ru.jerael.booktracker.backend.web.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.factory.genre.GenreDomainFactory

class GenreWebMapperTest {
    private val mapper = GenreWebMapper()
    
    @Test
    fun `toResponse should map Genre to GenreResponse`() {
        val genre = GenreDomainFactory.createGenre()
        
        val response = mapper.toResponse(genre)
        
        with(response) {
            assertEquals(genre.id, id)
            assertEquals(genre.name, name)
        }
    }
    
    @Test
    fun `toResponses should map Genres to GenreResponses`() {
        val genre1 = GenreDomainFactory.createGenre(1, "Genre 1")
        val genre2 = GenreDomainFactory.createGenre(2, "Genre 2")
        val genres = setOf(genre1, genre2)
        
        val responses = mapper.toResponses(genres)
        
        val expectedResponses = genres.map { mapper.toResponse(it) }.toSet()
        assertEquals(expectedResponses, responses)
    }
}