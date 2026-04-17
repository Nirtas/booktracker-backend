package ru.jerael.booktracker.backend.data.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.context.annotation.Import
import ru.jerael.booktracker.backend.data.db.repository.JpaLanguageRepository
import ru.jerael.booktracker.backend.data.mapper.LanguageDataMapper
import ru.jerael.booktracker.backend.factory.language.LanguageEntityFactory

@DataJpaTest
@Import(LanguageRepositoryImpl::class, LanguageDataMapper::class)
class LanguageRepositoryImplTest {
    
    @Autowired
    private lateinit var jpaLanguageRepository: JpaLanguageRepository
    
    @Autowired
    private lateinit var languageRepository: LanguageRepositoryImpl
    
    @Test
    fun `findAll should return list of languages`() {
        jpaLanguageRepository.saveAll(
            listOf(
                LanguageEntityFactory.createLanguageEntity(code = "en", name = "English"),
                LanguageEntityFactory.createLanguageEntity(code = "ru", name = "Русский")
            )
        )
        
        val result = languageRepository.findAll()
        
        with(result) {
            assertEquals(2, size)
            assertEquals("en", this[0].code)
            assertEquals("ru", this[1].code)
        }
    }
    
    @Test
    fun `when language exists, findByCode should return language`() {
        jpaLanguageRepository.save(LanguageEntityFactory.createLanguageEntity(code = "en", name = "English"))
        
        val result = languageRepository.findByCode("en")
        
        with(result.get()) {
            assertEquals("en", code)
            assertEquals("English", name)
        }
    }
}