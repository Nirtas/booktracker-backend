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
import ru.jerael.booktracker.backend.data.db.repository.JpaLanguageRepository
import ru.jerael.booktracker.backend.factory.language.LanguageEntityFactory

@SpringBootTest
@Import(TestcontainersConfiguration::class)
class LanguageCacheTest {
    
    @Autowired
    private lateinit var redisCacheManager: RedisCacheManager
    
    @MockitoSpyBean
    private lateinit var jpaLanguageRepository: JpaLanguageRepository
    
    @Autowired
    private lateinit var languageRepository: LanguageRepositoryImpl
    
    @BeforeEach
    fun setUp() {
        redisCacheManager.getCache("languages")?.clear()
        redisCacheManager.getCache("language")?.clear()
    }
    
    @Test
    fun `findAll should cache list of languages and not call database again`() {
        languageRepository.findAll()
        languageRepository.findAll()
        
        verify(jpaLanguageRepository, times(1)).findAllByOrderByNameAsc()
    }
    
    @Test
    fun `findByCode should cache language and not call database again`() {
        val code = jpaLanguageRepository.saveAndFlush(LanguageEntityFactory.createLanguageEntity()).code
        
        reset(jpaLanguageRepository)
        
        languageRepository.findByCode(code)
        languageRepository.findByCode(code)
        
        verify(jpaLanguageRepository, times(1)).findByCode(code)
    }
}