package ru.jerael.booktracker.backend.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import ru.jerael.booktracker.backend.api.dto.book.BookCreationRequest;
import ru.jerael.booktracker.backend.api.dto.book.BookDetailsUpdateRequest;
import ru.jerael.booktracker.backend.api.dto.book.BookResponse;
import ru.jerael.booktracker.backend.api.exception.handler.GlobalExceptionHandler;
import ru.jerael.booktracker.backend.api.mapper.BookApiMapper;
import ru.jerael.booktracker.backend.api.mapper.GenreApiMapper;
import ru.jerael.booktracker.backend.api.mapper.UploadCoverApiMapper;
import ru.jerael.booktracker.backend.api.validator.FileValidator;
import ru.jerael.booktracker.backend.domain.exception.factory.BookExceptionFactory;
import ru.jerael.booktracker.backend.domain.model.book.*;
import ru.jerael.booktracker.backend.domain.model.pagination.PageQuery;
import ru.jerael.booktracker.backend.domain.model.pagination.PageResult;
import ru.jerael.booktracker.backend.domain.usecase.book.*;
import tools.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(BookController.class)
@Import({GlobalExceptionHandler.class, BookApiMapper.class, GenreApiMapper.class})
class BookControllerTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GetBooksUseCase getBooksUseCase;

    @MockitoBean
    private GetBookByIdUseCase getBookByIdUseCase;

    @MockitoBean
    private CreateBookUseCase createBookUseCase;

    @MockitoBean
    private UploadCoverUseCase uploadCoverUseCase;

    @MockitoBean
    private DeleteCoverUseCase deleteCoverUseCase;

    @MockitoBean
    private DeleteBookUseCase deleteBookUseCase;

    @MockitoBean
    private UpdateBookDetailsUseCase updateBookDetailsUseCase;

    @MockitoBean
    private FileValidator fileValidator;

    @MockitoBean
    private BookApiMapper bookApiMapper;

    @MockitoBean
    private UploadCoverApiMapper uploadCoverApiMapper;

    private final UUID id = UUID.fromString("ee39af7a-a073-4473-878a-1aae34e98bb7");
    private final String title = "title";
    private final String author = "author";
    private final BookStatus status = BookStatus.READING;
    private final Instant createdAt = Instant.ofEpochMilli(1771249699347L);

    @Test
    void getAll_ShouldReturnListOfBookResponses() {
        Book book = new Book(id, title, author, null, status, createdAt, Collections.emptySet());
        BookResponse bookResponse = new BookResponse(id, title, author, null, status.getValue(),
            createdAt.toEpochMilli(), Collections.emptySet());
        PageResult<Book> pageResult = new PageResult<>(List.of(book), 10, 0, 1, 1);
        when(getBooksUseCase.execute(any(PageQuery.class))).thenReturn(pageResult);
        when(bookApiMapper.toResponse(book)).thenReturn(bookResponse);

        var response = mockMvcTester.get().uri("/api/v1/books?page=0&size=10").exchange();

        assertThat(response)
            .hasStatus(HttpStatus.OK)
            .bodyJson()
            .extractingPath("$.content")
            .convertTo(BookResponse[].class)
            .satisfies(responses -> assertThat(responses).containsExactly(bookResponse));

        assertThat(response)
            .bodyJson()
            .extractingPath("$.page.totalElements").isEqualTo(1);

        verify(getBooksUseCase).execute(any(PageQuery.class));
    }

    @Test
    void getById_WhenExceptionThrown_ShouldReturnNotFound() {
        UUID id = UUID.fromString("31d3f5e3-7faf-4678-a3cf-4657d8875a82");
        when(getBookByIdUseCase.execute(id)).thenThrow(BookExceptionFactory.notFound(id));

        assertThat(mockMvcTester.get().uri("/api/v1/books/" + id))
            .hasStatus(HttpStatus.NOT_FOUND)
            .bodyJson()
            .extractingPath("$.detail")
            .isEqualTo("Book with id " + id + " was not found");

        verify(getBookByIdUseCase).execute(id);
    }

    @Test
    void deleteBook_ShouldReturnNoContent() {
        var response = mockMvcTester.delete().uri("/api/v1/books/" + id);

        assertThat(response).hasStatus(HttpStatus.NO_CONTENT);
        verify(deleteBookUseCase).execute(id);
    }

    @Test
    void updateDetails_ShouldReturnUpdatedBook() {
        BookDetailsUpdateRequest request = new BookDetailsUpdateRequest(
            "new title",
            null,
            "reading",
            null
        );
        BookDetailsUpdate data = new BookDetailsUpdate("new title", null, BookStatus.READING, null);
        Book book = new Book(id, "new title", author, null, BookStatus.READING, createdAt, Collections.emptySet());
        BookResponse bookResponse =
            new BookResponse(id, "new title", author, null, "reading", createdAt.toEpochMilli(),
                Collections.emptySet());
        when(bookApiMapper.toDomain(request)).thenReturn(data);
        when(updateBookDetailsUseCase.execute(id, data)).thenReturn(book);
        when(bookApiMapper.toResponse(book)).thenReturn(bookResponse);

        assertThat(
            mockMvcTester.patch().uri("/api/v1/books/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .hasStatus(HttpStatus.OK)
            .bodyJson()
            .convertTo(BookResponse.class)
            .satisfies(response -> {
                assertThat(response.title()).isEqualTo(request.title());
                assertThat(response.author()).isEqualTo(author);
                assertThat(response.status()).isEqualTo(book.status().getValue());
            });
    }

    @Test
    void create_ShouldCreateBook() {
        BookCreationRequest request = new BookCreationRequest(title, author, Set.of(1, 2));
        BookCreation data = new BookCreation(title, author, Set.of(1, 2));
        Book book = new Book(id, title, author, null, status, createdAt, Collections.emptySet());
        BookResponse bookResponse =
            new BookResponse(id, title, author, null, status.getValue(), createdAt.toEpochMilli(),
                Collections.emptySet());
        when(bookApiMapper.toDomain(request)).thenReturn(data);
        when(createBookUseCase.execute(data)).thenReturn(book);
        when(bookApiMapper.toResponse(book)).thenReturn(bookResponse);

        assertThat(
            mockMvcTester.post().uri("/api/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .hasStatus(HttpStatus.CREATED)
            .bodyJson()
            .convertTo(BookResponse.class)
            .satisfies(response -> {
                    assertThat(response.id()).isEqualTo(bookResponse.id());
                    assertThat(response.title()).isEqualTo(bookResponse.title());
                    assertThat(response.author()).isEqualTo(bookResponse.author());
                }
            );
    }

    @Test
    void uploadCover_ShouldUploadCover() {
        String coverUrl = "covers/cover.jpg";
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
            "cover",
            "image.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "content".getBytes()
        );
        UploadCover data = new UploadCover(id, MediaType.IMAGE_JPEG_VALUE, null);
        Book book = new Book(id, title, author, coverUrl, status, createdAt, null);
        BookResponse bookResponse =
            new BookResponse(id, title, author, coverUrl, status.getValue(), createdAt.toEpochMilli(), null);
        when(uploadCoverApiMapper.toDomain(id, mockMultipartFile)).thenReturn(data);
        when(uploadCoverUseCase.execute(data)).thenReturn(book);
        when(bookApiMapper.toResponse(book)).thenReturn(bookResponse);

        assertThat(
            mockMvcTester.patch().uri("/api/v1/books/" + id + "/cover")
                .multipart()
                .file(mockMultipartFile)
        )
            .hasStatus(HttpStatus.OK)
            .bodyJson()
            .convertTo(BookResponse.class)
            .satisfies(response -> assertThat(response.coverUrl()).isEqualTo(bookResponse.coverUrl()));
    }

    @Test
    void deleteCover_ShouldReturnNoContent() {
        var response = mockMvcTester.delete().uri("/api/v1/books/" + id + "/cover");

        assertThat(response).hasStatus(HttpStatus.NO_CONTENT);
        verify(deleteCoverUseCase).execute(id);
    }
}