package ru.jerael.booktracker.backend.data.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.context.annotation.Import
import ru.jerael.booktracker.backend.data.db.repository.JpaGenreRepository
import ru.jerael.booktracker.backend.data.mapper.GenreDataMapper
import ru.jerael.booktracker.backend.factory.genre.GenreEntityFactory

@DataJpaTest
@Import(GenreRepositoryImpl::class, GenreDataMapper::class)
class GenreRepositoryImplTest {
    
    @Autowired
    private lateinit var jpaGenreRepository: JpaGenreRepository
    
    @Autowired
    private lateinit var genreRepository: GenreRepositoryImpl
    
    @Test
    fun `findAll should return all genres`() {
        jpaGenreRepository.saveAll(
            listOf(
                GenreEntityFactory.createGenreEntity(name = "Genre 1"),
                GenreEntityFactory.createGenreEntity(name = "Genre 2")
            )
        )
        
        val result = genreRepository.findAll()
        
        with(result) {
            assertEquals(2, size)
            assertThat(this).extracting("name").containsExactlyInAnyOrder("Genre 1", "Genre 2")
        }
    }
    
    @Test
    fun `when genre exists, findById should return genre`() {
        val savedGenre = jpaGenreRepository.save(GenreEntityFactory.createGenreEntity())
        
        val result = genreRepository.findById(savedGenre.id)
        
        with(result.get()) {
            assertEquals(savedGenre.name, name)
            assertEquals(savedGenre.id, id)
        }
    }
    
    @Test
    fun `findAllById should return set of existing genres`() {
        val savedGenres = jpaGenreRepository.saveAll(
            listOf(
                GenreEntityFactory.createGenreEntity(name = "Genre 1"),
                GenreEntityFactory.createGenreEntity(name = "Genre 2"),
                GenreEntityFactory.createGenreEntity(name = "Genre 3")
            )
        )
        val genreIds = setOf(savedGenres[0].id, savedGenres[1].id, 5555)
        
        val result = genreRepository.findAllById(genreIds)
        
        with(result) {
            assertEquals(2, size)
            assertThat(this).extracting("name").containsExactlyInAnyOrder("Genre 1", "Genre 2")
        }
    }
    
    @Test
    fun `findAllByNames should return set of existing genres`() {
        jpaGenreRepository.saveAll(
            listOf(
                GenreEntityFactory.createGenreEntity(name = "Genre 1"),
                GenreEntityFactory.createGenreEntity(name = "Genre 2"),
                GenreEntityFactory.createGenreEntity(name = "Genre 3")
            )
        )
        val genreNames = setOf("genre 1", "genre 2", "unknown genre")
        
        val result = genreRepository.findAllByNames(genreNames)
        
        with(result) {
            assertThat(size).isEqualTo(2)
            assertThat(this).extracting("name").containsExactlyInAnyOrder("Genre 1", "Genre 2")
        }
    }
}