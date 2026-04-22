package ru.jerael.booktracker.backend.data.repository

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import ru.jerael.booktracker.backend.config.TestcontainersConfiguration
import ru.jerael.booktracker.backend.data.db.repository.JpaGenreRepository
import ru.jerael.booktracker.backend.factory.genre.GenreEntityFactory

@SpringBootTest
@Import(TestcontainersConfiguration::class)
class GenreCacheTest {
    
    @Autowired
    private lateinit var redisCacheManager: RedisCacheManager
    
    @MockitoSpyBean
    private lateinit var jpaGenreRepository: JpaGenreRepository
    
    @Autowired
    private lateinit var genreRepository: GenreRepositoryImpl
    
    @BeforeEach
    fun setUp() {
        redisCacheManager.getCache("genres")?.clear()
        redisCacheManager.getCache("genre")?.clear()
    }
    
    @Test
    fun `findAll should cache set of genres and not call database again`() {
        genreRepository.findAll()
        genreRepository.findAll()
        
        verify(jpaGenreRepository, times(1)).findAllByOrderByNameAsc()
    }
    
    @Test
    fun `findById should cache genre and not call database again`() {
        val genreId = jpaGenreRepository.save(GenreEntityFactory.createGenreEntity()).id
        
        reset(jpaGenreRepository)
        
        genreRepository.findById(genreId)
        genreRepository.findById(genreId)
        
        verify(jpaGenreRepository, times(1)).findById(genreId)
    }
}