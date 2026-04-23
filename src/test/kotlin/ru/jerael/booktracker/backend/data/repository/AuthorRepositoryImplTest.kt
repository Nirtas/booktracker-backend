package ru.jerael.booktracker.backend.data.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.context.annotation.Import
import ru.jerael.booktracker.backend.data.db.repository.JpaAuthorRepository
import ru.jerael.booktracker.backend.data.mapper.AuthorDataMapper
import ru.jerael.booktracker.backend.factory.author.AuthorDomainFactory
import ru.jerael.booktracker.backend.factory.author.AuthorEntityFactory

@DataJpaTest
@Import(AuthorRepositoryImpl::class, AuthorDataMapper::class)
class AuthorRepositoryImplTest {
    
    @Autowired
    private lateinit var jpaAuthorRepository: JpaAuthorRepository
    
    @Autowired
    private lateinit var authorRepository: AuthorRepositoryImpl
    
    @Test
    fun `when author exists, findByFullName should return author`() {
        val fullName = "Author A"
        val entity = AuthorEntityFactory.createAuthorEntity(id = null, fullName = fullName)
        
        val savedEntity = jpaAuthorRepository.save(entity)
        
        val result = authorRepository.findByFullName(fullName)
        
        assertTrue(result.isPresent)
        assertEquals(savedEntity.id, result.get().id)
        assertEquals(fullName, result.get().fullName)
    }
    
    @Test
    fun `when id is null, save should insert new author`() {
        val author = AuthorDomainFactory.createAuthor(id = null)
        
        val result = authorRepository.save(author)
        
        assertNotNull(result.id)
        assertEquals(author.fullName, result.fullName)
    }
    
    @Test
    fun `when id is present, save should update existing author`() {
        val author = AuthorDomainFactory.createAuthor(id = null)
        
        val savedId = authorRepository.save(author).id
        
        val updatedAuthor = AuthorDomainFactory.createAuthor(id = savedId, fullName = "Author B")
        
        val result = authorRepository.save(updatedAuthor)
        
        assertEquals(savedId, result.id)
        assertEquals("Author B", result.fullName)
    }
}