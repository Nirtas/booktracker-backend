package ru.jerael.booktracker.backend.data.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.factory.genre.GenreDomainFactory
import ru.jerael.booktracker.backend.factory.genre.GenreEntityFactory

class GenreDataMapperTest {
    private val mapper = GenreDataMapper()
    
    @Test
    fun `toDomain should map GenreEntity to Genre`() {
        val entity = GenreEntityFactory.createGenreEntity()
        
        val genre = mapper.toDomain(entity)
        
        with(genre) {
            assertEquals(entity.id, id)
            assertEquals(entity.name, name)
        }
    }
    
    @Test
    fun `toEntity should map Genre to GenreEntity`() {
        val genre = GenreDomainFactory.createGenre()
        
        val entity = mapper.toEntity(genre)
        
        with(entity) {
            assertEquals(genre.id, id)
            assertEquals(genre.name, name)
        }
    }
}