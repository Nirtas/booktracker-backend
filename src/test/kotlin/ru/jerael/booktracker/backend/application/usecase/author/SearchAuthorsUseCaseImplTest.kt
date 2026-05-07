package ru.jerael.booktracker.backend.application.usecase.author

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import ru.jerael.booktracker.backend.domain.model.pagination.PageQuery
import ru.jerael.booktracker.backend.domain.model.pagination.PageResult
import ru.jerael.booktracker.backend.domain.repository.AuthorRepository
import ru.jerael.booktracker.backend.factory.author.AuthorDomainFactory

@ExtendWith(MockKExtension::class)
class SearchAuthorsUseCaseImplTest {
    
    @MockK
    private lateinit var authorRepository: AuthorRepository
    
    @InjectMockKs
    private lateinit var useCase: SearchAuthorsUseCaseImpl
    
    @Test
    fun `when query is too short, execute should return empty PageResult`() {
        val queries = listOf("", "  ", "a", "aa", "  a  ")
        val pageQuery = PageQuery(0, 5, null, null)
        
        queries.forEach { query ->
            val result = useCase.execute(pageQuery, query)
            
            assertThat(result.content).isEmpty()
            verify(exactly = 0) { authorRepository.searchByFullName(any(), any()) }
        }
    }
    
    @Test
    fun `when found authors in repository, execute should return PageResult`() {
        val author = AuthorDomainFactory.createAuthor(fullName = "Author A")
        val query = "hor"
        val pageQuery = PageQuery(0, 5, null, null)
        val pageResult = PageResult(listOf(author), 1, 0, 1, 1)
        
        every { authorRepository.searchByFullName(pageQuery, query) } returns pageResult
        
        val result = useCase.execute(pageQuery, query)
        
        with(result) {
            assertThat(content).hasSize(1)
            assertThat(content[0].fullName).isEqualTo(author.fullName)
        }
    }
}