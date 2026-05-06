package ru.jerael.booktracker.backend.data.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.context.annotation.Import
import ru.jerael.booktracker.backend.data.db.repository.JpaAuthorRepository
import ru.jerael.booktracker.backend.data.mapper.AuthorDataMapper
import ru.jerael.booktracker.backend.domain.model.pagination.PageQuery
import ru.jerael.booktracker.backend.domain.model.pagination.SortDirection
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
    
    @Test
    fun `findAllByNames should return set of existing authors`() {
        jpaAuthorRepository.saveAll(
            listOf(
                AuthorEntityFactory.createAuthorEntity(id = null, fullName = "Author A"),
                AuthorEntityFactory.createAuthorEntity(id = null, fullName = "Author B"),
                AuthorEntityFactory.createAuthorEntity(id = null, fullName = "Author C")
            )
        )
        val authorNames = setOf("author a", "author b", "new author")
        
        val result = authorRepository.findAllByNames(authorNames)
        
        with(result) {
            assertThat(size).isEqualTo(2)
            assertThat(this).extracting("fullName").containsExactlyInAnyOrder("Author A", "Author B")
        }
    }
    
    @Test
    fun `searchByFullName should return list of found authors by similarity`() {
        jpaAuthorRepository.saveAll(
            listOf(
                AuthorEntityFactory.createAuthorEntity(id = null, fullName = "Author A"),
                AuthorEntityFactory.createAuthorEntity(id = null, fullName = "Brandon Sanderson"),
                AuthorEntityFactory.createAuthorEntity(id = null, fullName = "Robert Jordan")
            )
        )
        val query = "er"
        val pageQuery = PageQuery(0, 5, "fullName", SortDirection.ASC)
        
        val result = authorRepository.searchByFullName(pageQuery, query)
        
        with(result) {
            assertThat(content).hasSize(2)
            
            val authorNames = content.map { it.fullName }
            assertThat(authorNames).containsExactlyInAnyOrder("Brandon Sanderson", "Robert Jordan")
            assertThat(authorNames).doesNotContain("Author A")
            
            assertThat(totalElements).isEqualTo(2)
        }
    }
}