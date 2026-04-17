package ru.jerael.booktracker.backend.data.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.context.annotation.Import
import ru.jerael.booktracker.backend.data.db.repository.JpaBookRepository
import ru.jerael.booktracker.backend.data.db.repository.JpaGenreRepository
import ru.jerael.booktracker.backend.data.db.repository.JpaUserRepository
import ru.jerael.booktracker.backend.data.mapper.*
import ru.jerael.booktracker.backend.domain.model.pagination.PageQuery
import ru.jerael.booktracker.backend.domain.model.pagination.SortDirection
import ru.jerael.booktracker.backend.factory.book.BookDomainFactory
import ru.jerael.booktracker.backend.factory.book.BookEntityFactory
import ru.jerael.booktracker.backend.factory.user.UserEntityFactory

@DataJpaTest
@Import(
    BookRepositoryImpl::class,
    GenreRepositoryImpl::class,
    GenreDataMapper::class,
    AuthorDataMapper::class,
    PublisherDataMapper::class,
    LanguageDataMapper::class,
    ReadingSessionDataMapper::class,
    ReadingAttemptDataMapper::class,
    NoteDataMapper::class,
    BookDataMapper::class
)
class BookRepositoryImplTest {
    
    @Autowired
    private lateinit var jpaBookRepository: JpaBookRepository
    
    @Autowired
    private lateinit var jpaGenreRepository: JpaGenreRepository
    
    @Autowired
    private lateinit var jpaUserRepository: JpaUserRepository
    
    @Autowired
    private lateinit var bookRepository: BookRepositoryImpl
    
    @Test
    fun `findAllByUserId should return all user's books`() {
        val user = jpaUserRepository.save(UserEntityFactory.createUserEntity(email = "user1@example.com"))
        val anotherUser = jpaUserRepository.save(UserEntityFactory.createUserEntity(email = "user2@example.com"))
        
        val book1 = BookEntityFactory.createBookEntity(user = user)
        val book2 = BookEntityFactory.createBookEntity(user = user)
        val book3 = BookEntityFactory.createBookEntity(user = anotherUser)
        
        val savedBooks = jpaBookRepository.saveAll(listOf(book1, book2, book3))
        val id1 = savedBooks[0].id
        val id2 = savedBooks[1].id
        
        val query = PageQuery(0, 10, "createdAt", SortDirection.DESC)
        
        val result = bookRepository.findAllByUserId(query, user.id)
        
        with(result) {
            assertEquals(2, content.size)
            assertThat(content).extracting("id").containsExactlyInAnyOrder(id1, id2)
        }
    }
    
    @Test
    fun `when book exists, findByIdAndUserId should return book`() {
        val user = jpaUserRepository.save(UserEntityFactory.createUserEntity())
        val book = jpaBookRepository.save(BookEntityFactory.createBookEntity(user = user))
        
        val result = bookRepository.findByIdAndUserId(book.id, user.id)
        
        with(result) {
            assertTrue(isPresent)
            assertEquals(book.id, get().id)
            assertEquals(user.id, get().userId)
        }
    }
    
    @Test
    fun `when id is null, save should insert new book`() {
        val user = jpaUserRepository.save(UserEntityFactory.createUserEntity())
        val book = BookDomainFactory.createBook(
            id = null,
            userId = user.id
        )
        
        val result = bookRepository.save(book, user.id)
        
        with(result) {
            assertNotNull(result.id)
            assertEquals(book.title, title)
            assertTrue(jpaBookRepository.existsById(id))
        }
    }
    
    @Test
    fun `when id is present, save should update existing book`() {
        val user = jpaUserRepository.save(UserEntityFactory.createUserEntity())
        val savedBook = jpaBookRepository.save(BookEntityFactory.createBookEntity(user = user))
        val updatedBook = BookDomainFactory.createBook(
            id = savedBook.id,
            userId = user.id,
            title = "new title"
        )
        
        val result = bookRepository.save(updatedBook, user.id)
        
        with(result) {
            assertEquals(updatedBook.id, id)
            assertEquals(updatedBook.title, title)
            assertTrue(jpaBookRepository.existsById(id))
        }
    }
    
    @Test
    fun `deleteByIdAndUserId should delete user's book by id`() {
        val user = jpaUserRepository.save(UserEntityFactory.createUserEntity())
        val book = jpaBookRepository.save(BookEntityFactory.createBookEntity(user = user))
        
        assertTrue(jpaBookRepository.existsById(book.id))
        
        bookRepository.deleteByIdAndUserId(book.id, user.id)
        
        assertFalse(jpaBookRepository.existsById(book.id))
    }
}