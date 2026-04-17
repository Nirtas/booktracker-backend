package ru.jerael.booktracker.backend.application.usecase.genre

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import ru.jerael.booktracker.backend.domain.model.Genre
import ru.jerael.booktracker.backend.domain.repository.GenreRepository

@ExtendWith(MockKExtension::class)
class GetGenresUseCaseImplTest {
    
    @MockK
    private lateinit var genreRepository: GenreRepository
    
    @InjectMockKs
    private lateinit var useCase: GetGenresUseCaseImpl
    
    @Test
    fun `execute should return set of genres`() {
        val genres = setOf(
            Genre(1, "Genre 1"),
            Genre(2, "Genre 2")
        )
        
        every { genreRepository.findAll() } returns genres
        
        with(useCase.execute()) {
            assertEquals(genres.size, size)
            assertEquals(genres, this)
        }
    }
}