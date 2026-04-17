package ru.jerael.booktracker.backend.application.usecase.genre

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import ru.jerael.booktracker.backend.domain.exception.NotFoundException
import ru.jerael.booktracker.backend.domain.repository.GenreRepository
import ru.jerael.booktracker.backend.factory.genre.GenreDomainFactory
import java.util.*

@ExtendWith(MockKExtension::class)
class GetGenreByIdUseCaseImplTest {
    
    @MockK
    private lateinit var genreRepository: GenreRepository
    
    @InjectMockKs
    private lateinit var useCase: GetGenreByIdUseCaseImpl
    
    @Test
    fun `when genre exists, execute should return genre`() {
        val genreId = 1
        val genre = GenreDomainFactory.createGenre(id = genreId)
        
        every { genreRepository.findById(genreId) } returns Optional.of(genre)
        
        val result = useCase.execute(genreId)
        
        assertEquals(genre, result)
        
        verify { genreRepository.findById(genreId) }
    }
    
    @Test
    fun `when genre does not exists, execute should throw NotFoundException`() {
        val genreId = 5555
        
        every { genreRepository.findById(genreId) } returns Optional.empty()
        
        assertThrows(NotFoundException::class.java) { useCase.execute(genreId) }
    }
}