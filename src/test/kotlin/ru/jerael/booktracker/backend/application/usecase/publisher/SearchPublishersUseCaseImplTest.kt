package ru.jerael.booktracker.backend.application.usecase.publisher

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
import ru.jerael.booktracker.backend.domain.repository.PublisherRepository
import ru.jerael.booktracker.backend.factory.publisher.PublisherDomainFactory

@ExtendWith(MockKExtension::class)
class SearchPublishersUseCaseImplTest {
    
    @MockK
    private lateinit var publisherRepository: PublisherRepository
    
    @InjectMockKs
    private lateinit var useCase: SearchPublishersUseCaseImpl
    
    @Test
    fun `when query is too short, execute should return empty PageResult`() {
        val queries = listOf("", "  ", "a", "aa", "  a  ")
        val pageQuery = PageQuery(0, 5, null, null)
        
        queries.forEach { query ->
            val result = useCase.execute(pageQuery, query)
            
            assertThat(result.content).isEmpty()
            verify(exactly = 0) { publisherRepository.searchByName(any(), any()) }
        }
    }
    
    @Test
    fun `when found publishers in repository, execute should return PageResult`() {
        val publisher = PublisherDomainFactory.createPublisher(name = "Publisher A")
        val query = "she"
        val pageQuery = PageQuery(0, 5, null, null)
        val pageResult = PageResult(listOf(publisher), 1, 0, 1, 1)
        
        every { publisherRepository.searchByName(pageQuery, query) } returns pageResult
        
        val result = useCase.execute(pageQuery, query)
        
        with(result) {
            assertThat(content).hasSize(1)
            assertThat(content[0].name).isEqualTo(publisher.name)
        }
    }
}