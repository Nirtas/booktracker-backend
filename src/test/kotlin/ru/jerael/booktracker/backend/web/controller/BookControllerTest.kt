package ru.jerael.booktracker.backend.web.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication
import org.springframework.test.web.servlet.assertj.MockMvcTester
import ru.jerael.booktracker.backend.domain.exception.factory.BookExceptionFactory
import ru.jerael.booktracker.backend.domain.model.book.BookStatus
import ru.jerael.booktracker.backend.domain.model.pagination.PageResult
import ru.jerael.booktracker.backend.domain.service.token.AuthTokenService
import ru.jerael.booktracker.backend.domain.usecase.book.*
import ru.jerael.booktracker.backend.factory.auth.AuthWebFactory
import ru.jerael.booktracker.backend.factory.book.BookDomainFactory
import ru.jerael.booktracker.backend.factory.book.BookWebFactory
import ru.jerael.booktracker.backend.factory.file.FileFactory
import ru.jerael.booktracker.backend.factory.image.ImageDomainFactory
import ru.jerael.booktracker.backend.web.config.WebProperties
import ru.jerael.booktracker.backend.web.dto.book.BookResponse
import ru.jerael.booktracker.backend.web.exception.handler.GlobalExceptionHandler
import ru.jerael.booktracker.backend.web.mapper.BookWebMapper
import ru.jerael.booktracker.backend.web.mapper.UploadCoverWebMapper
import ru.jerael.booktracker.backend.web.security.SecurityConfig
import ru.jerael.booktracker.backend.web.validator.FileValidator
import tools.jackson.databind.ObjectMapper
import java.util.*

@WebMvcTest(BookController::class)
@Import(GlobalExceptionHandler::class, SecurityConfig::class)
class BookControllerTest {
    
    @Autowired
    private lateinit var mockMvcTester: MockMvcTester
    
    @MockkBean
    private lateinit var webProperties: WebProperties
    
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    
    @MockkBean
    private lateinit var getBooksUseCase: GetBooksUseCase
    
    @MockkBean
    private lateinit var getBookByIdUseCase: GetBookByIdUseCase
    
    @MockkBean
    private lateinit var createBookUseCase: CreateBookUseCase
    
    @MockkBean
    private lateinit var uploadCoverUseCase: UploadCoverUseCase
    
    @MockkBean
    private lateinit var deleteCoverUseCase: DeleteCoverUseCase
    
    @MockkBean
    private lateinit var deleteBookUseCase: DeleteBookUseCase
    
    @MockkBean
    private lateinit var updateBookDetailsUseCase: UpdateBookDetailsUseCase
    
    @MockkBean
    private lateinit var getBookCoverUseCase: GetBookCoverUseCase
    
    @MockkBean
    private lateinit var fileValidator: FileValidator
    
    @MockkBean
    private lateinit var bookWebMapper: BookWebMapper
    
    @MockkBean
    private lateinit var uploadCoverWebMapper: UploadCoverWebMapper
    
    @MockkBean
    private lateinit var authTokenService: AuthTokenService
    
    private val userId: UUID = UUID.randomUUID()
    private val bookId: UUID = UUID.randomUUID()
    
    @Test
    fun `getAll should return list of BookResponses`() {
        val book = BookDomainFactory.createBook(userId = userId)
        val bookResponse = BookWebFactory.createBookResponse()
        val pageResult = PageResult(listOf(book), 10, 0, 1, 1)
        
        every { getBooksUseCase.execute(any(), eq(userId)) } returns pageResult
        every { bookWebMapper.toResponse(book) } returns bookResponse
        
        val mockResponse = mockMvcTester.get().uri("/api/v1/books?page=0&size=10")
            .with(authentication(AuthWebFactory.createAuthToken(userId = userId))).exchange()
        
        assertThat(mockResponse)
            .hasStatus(HttpStatus.OK)
            .bodyJson().apply {
                extractingPath("$.content")
                    .convertTo(Array<BookResponse>::class.java)
                    .satisfies({ responses ->
                        assertThat(responses).containsExactly(bookResponse)
                    })
                extractingPath("$.page.totalElements").isEqualTo(1)
            }
        
        verify { getBooksUseCase.execute(any(), eq(userId)) }
    }
    
    @Test
    fun `when Book not found, getById should return 404 Not Found`() {
        every { getBookByIdUseCase.execute(bookId, userId) } throws BookExceptionFactory.bookNotFound(bookId)
        
        val mockResponse = mockMvcTester.get().uri("/api/v1/books/$bookId")
            .with(authentication(AuthWebFactory.createAuthToken(userId = userId)))
        
        assertThat(mockResponse)
            .hasStatus(HttpStatus.NOT_FOUND)
            .bodyJson().apply {
                extractingPath("$.detail").isEqualTo("Book with id $bookId was not found")
            }
        
        verify { getBookByIdUseCase.execute(bookId, userId) }
    }
    
    @Test
    fun `deleteBook should return 204 NO CONTENT`() {
        every { deleteBookUseCase.execute(bookId, userId) } just Runs
        
        val mockResponse = mockMvcTester.delete().uri("/api/v1/books/$bookId")
            .with(authentication(AuthWebFactory.createAuthToken(userId = userId)))
        
        assertThat(mockResponse).hasStatus(HttpStatus.NO_CONTENT)
        
        verify { deleteBookUseCase.execute(bookId, userId) }
    }
    
    @Test
    fun `updateDetails should return updated Book`() {
        val newTitle = "New Title"
        val newStatus = BookStatus.COMPLETED.value
        
        val request = BookWebFactory.createBookDetailsUpdateRequest(title = newTitle, status = newStatus)
        val data = BookDomainFactory.createBookDetailsUpdate(
            bookId = bookId,
            userId = userId,
            title = newTitle,
            status = BookStatus.fromString(newStatus)
        )
        val book = BookDomainFactory.createBook(id = bookId, userId = userId, title = newTitle)
        val bookResponse = BookWebFactory.createBookResponse(id = bookId, title = newTitle, status = newStatus)
        
        every { bookWebMapper.toDomain(request, bookId, userId) } returns data
        every { updateBookDetailsUseCase.execute(data) } returns book
        every { bookWebMapper.toResponse(book) } returns bookResponse
        
        val mockResponse = mockMvcTester.patch().uri("/api/v1/books/$bookId")
            .with(authentication(AuthWebFactory.createAuthToken(userId = userId)))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        
        assertThat(mockResponse)
            .hasStatus(HttpStatus.OK)
            .bodyJson()
            .convertTo(BookResponse::class.java)
            .satisfies({ response ->
                assertEquals(newTitle, response.title)
                assertEquals(newStatus, response.status)
            })
        
        verify { updateBookDetailsUseCase.execute(data) }
    }
    
    @Test
    fun `create should return created Book`() {
        val title = " title "
        val cleanedTitle = "title"
        
        val request = BookWebFactory.createBookCreationRequest(title = title)
        val data = BookDomainFactory.createBookCreation(userId = userId, title = title)
        val book = BookDomainFactory.createBook(id = bookId, userId = userId, title = cleanedTitle)
        val bookResponse = BookWebFactory.createBookResponse(id = bookId, title = cleanedTitle)
        
        every { bookWebMapper.toDomain(request, userId) } returns data
        every { createBookUseCase.execute(data) } returns book
        every { bookWebMapper.toResponse(book) } returns bookResponse
        
        val mockResponse = mockMvcTester.post().uri("/api/v1/books")
            .with(authentication(AuthWebFactory.createAuthToken(userId = userId)))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        
        assertThat(mockResponse)
            .hasStatus(HttpStatus.CREATED)
            .bodyJson()
            .convertTo(BookResponse::class.java)
            .satisfies({ response ->
                assertEquals(bookId, response.id)
                assertEquals(cleanedTitle, response.title)
            })
        
        verify { createBookUseCase.execute(data) }
    }
    
    @Test
    fun `uploadCover should return updated Book`() {
        val fileName = "cover.jpg"
        val coverUrl = "http://localhost:8080/api/v1/books/$bookId/cover"
        
        val file = FileFactory.createMockMultipartFile(originalFileName = fileName)
        val data = BookDomainFactory.createUploadCoverPayload(
            contentType = file.contentType,
            content = file.inputStream,
            size = file.size
        )
        val book = BookDomainFactory.createBook(id = bookId, userId = userId, coverFileName = fileName)
        val bookResponse = BookWebFactory.createBookResponse(id = bookId, coverUrl = coverUrl)
        
        every { fileValidator.validate(file, any()) } just Runs
        every { uploadCoverWebMapper.toDomain(file, bookId, userId) } returns data
        every { uploadCoverUseCase.execute(data) } returns book
        every { bookWebMapper.toResponse(book) } returns bookResponse
        
        val mockResponse = mockMvcTester.post().uri("/api/v1/books/$bookId/cover")
            .with(authentication(AuthWebFactory.createAuthToken(userId = userId)))
            .multipart()
            .file(file)
        
        assertThat(mockResponse)
            .hasStatus(HttpStatus.OK)
            .bodyJson()
            .convertTo(BookResponse::class.java)
            .satisfies({ response ->
                assertEquals(coverUrl, response.coverUrl)
            })
        
        verify { uploadCoverUseCase.execute(data) }
    }
    
    @Test
    fun `deleteCover should return 204 NO CONTENT`() {
        every { deleteCoverUseCase.execute(bookId, userId) } just Runs
        
        val mockResponse = mockMvcTester.delete().uri("/api/v1/books/$bookId/cover")
            .with(authentication(AuthWebFactory.createAuthToken(userId = userId)))
        
        assertThat(mockResponse).hasStatus(HttpStatus.NO_CONTENT)
        
        verify { deleteCoverUseCase.execute(bookId, userId) }
    }
    
    @Test
    fun `getCover should return ImageFile`() {
        val content = "content".toByteArray()
        
        val file = ImageDomainFactory.createImageFile(content = content.inputStream(), size = content.size.toLong())
        
        every { getBookCoverUseCase.execute(bookId, userId) } returns file
        
        val mockResponse = mockMvcTester.get().uri("/api/v1/books/$bookId/cover")
            .with(authentication(AuthWebFactory.createAuthToken(userId = userId)))
        
        assertThat(mockResponse)
            .hasStatus(HttpStatus.OK)
            .hasContentType(MediaType.parseMediaType(file.contentType))
            .hasHeader(HttpHeaders.CONTENT_DISPOSITION, "filename=\"${file.fileName}\"")
            .hasHeader(HttpHeaders.CACHE_CONTROL, "max-age=86400")
            .body().isEqualTo(content)
    }
}