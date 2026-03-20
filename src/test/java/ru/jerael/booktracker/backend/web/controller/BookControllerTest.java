package ru.jerael.booktracker.backend.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import ru.jerael.booktracker.backend.domain.exception.factory.BookExceptionFactory;
import ru.jerael.booktracker.backend.domain.model.book.*;
import ru.jerael.booktracker.backend.domain.model.image.ImageFile;
import ru.jerael.booktracker.backend.domain.model.pagination.PageQuery;
import ru.jerael.booktracker.backend.domain.model.pagination.PageResult;
import ru.jerael.booktracker.backend.domain.service.token.AuthTokenService;
import ru.jerael.booktracker.backend.domain.usecase.book.*;
import ru.jerael.booktracker.backend.web.config.WebProperties;
import ru.jerael.booktracker.backend.web.dto.book.BookCreationRequest;
import ru.jerael.booktracker.backend.web.dto.book.BookDetailsUpdateRequest;
import ru.jerael.booktracker.backend.web.dto.book.BookResponse;
import ru.jerael.booktracker.backend.web.exception.handler.GlobalExceptionHandler;
import ru.jerael.booktracker.backend.web.mapper.BookWebMapper;
import ru.jerael.booktracker.backend.web.mapper.GenreWebMapper;
import ru.jerael.booktracker.backend.web.mapper.UploadCoverWebMapper;
import ru.jerael.booktracker.backend.web.security.SecurityConfig;
import ru.jerael.booktracker.backend.web.validator.FileValidator;
import tools.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

@WebMvcTest(BookController.class)
@Import({GlobalExceptionHandler.class, BookWebMapper.class, GenreWebMapper.class, SecurityConfig.class})
class BookControllerTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private WebProperties webProperties;

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
    private GetBookCoverUseCase getBookCoverUseCase;

    @MockitoBean
    private FileValidator fileValidator;

    @MockitoBean
    private BookWebMapper bookWebMapper;

    @MockitoBean
    private UploadCoverWebMapper uploadCoverWebMapper;

    @MockitoBean
    private AuthTokenService authTokenService;

    private final UUID id = UUID.fromString("ee39af7a-a073-4473-878a-1aae34e98bb7");
    private final UUID userId = UUID.fromString("2c5781ea-1bc2-4561-a83d-26106df2526e");
    private final String title = "title";
    private final String author = "author";
    private final BookStatus status = BookStatus.READING;
    private final Instant createdAt = Instant.ofEpochMilli(1771249699347L);

    private UsernamePasswordAuthenticationToken getAuth() {
        return new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
    }

    @Test
    void getAll_ShouldReturnListOfBookResponses() {
        Book book = new Book(
            id,
            title,
            null,
            createdAt,
            Collections.emptySet(),
            Set.of(),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            List.of(),
            List.of()
        );
        BookResponse bookResponse = new BookResponse(
            id,
            title,
            null,
            status.getValue(),
            createdAt,
            Collections.emptySet(),
            Set.of(),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            List.of(),
            List.of()
        );

        PageResult<Book> pageResult = new PageResult<>(List.of(book), 10, 0, 1, 1);
        when(getBooksUseCase.execute(any(PageQuery.class), eq(userId))).thenReturn(pageResult);
        when(bookWebMapper.toResponse(book)).thenReturn(bookResponse);

        var mockResponse = mockMvcTester.get().uri("/api/v1/books?page=0&size=10")
            .with(authentication(getAuth())).exchange();

        assertThat(mockResponse)
            .hasStatus(HttpStatus.OK)
            .bodyJson()
            .extractingPath("$.content")
            .convertTo(BookResponse[].class)
            .satisfies(responses -> assertThat(responses).containsExactly(bookResponse));

        assertThat(mockResponse)
            .bodyJson()
            .extractingPath("$.page.totalElements").isEqualTo(1);

        verify(getBooksUseCase).execute(any(PageQuery.class), eq(userId));
    }

    @Test
    void getById_WhenExceptionThrown_ShouldReturnNotFound() {
        UUID id = UUID.fromString("31d3f5e3-7faf-4678-a3cf-4657d8875a82");
        when(getBookByIdUseCase.execute(id, userId)).thenThrow(BookExceptionFactory.bookNotFound(id));

        var mockResponse = mockMvcTester.get().uri("/api/v1/books/" + id).with(authentication(getAuth()));

        assertThat(mockResponse)
            .hasStatus(HttpStatus.NOT_FOUND)
            .bodyJson()
            .extractingPath("$.detail")
            .isEqualTo("Book with id " + id + " was not found");

        verify(getBookByIdUseCase).execute(id, userId);
    }

    @Test
    void deleteBook_ShouldReturnNoContent() {
        var mockResponse = mockMvcTester.delete().uri("/api/v1/books/" + id).with(authentication(getAuth()));

        assertThat(mockResponse).hasStatus(HttpStatus.NO_CONTENT);
        verify(deleteBookUseCase).execute(id, userId);
    }

    @Test
    void updateDetails_ShouldReturnUpdatedBook() {
        BookDetailsUpdateRequest request = new BookDetailsUpdateRequest(
            "new title",
            null,
            "want_to_read",
            null
        );
        BookDetailsUpdate data = new BookDetailsUpdate("new title", null, BookStatus.WANT_TO_READ, null);
        Book book = new Book(
            id,
            "new title",
            null,
            createdAt,
            Collections.emptySet(),
            Set.of(),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            List.of(),
            List.of()
        );
        BookResponse bookResponse = new BookResponse(
            id,
            "new title",
            null,
            "want_to_read",
            createdAt,
            Collections.emptySet(),
            Set.of(),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            List.of(),
            List.of()
        );
        when(bookWebMapper.toDomain(request)).thenReturn(data);
        when(updateBookDetailsUseCase.execute(id, userId, data)).thenReturn(book);
        when(bookWebMapper.toResponse(book)).thenReturn(bookResponse);

        var mockResponse = mockMvcTester.patch().uri("/api/v1/books/" + id)
            .with(authentication(getAuth()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request));

        assertThat(mockResponse)
            .hasStatus(HttpStatus.OK)
            .bodyJson()
            .convertTo(BookResponse.class)
            .satisfies(response -> {
                assertThat(response.title()).isEqualTo(request.title());
                assertThat(response.status()).isEqualTo(book.status().getValue());
            });
    }

    @Test
    void create_ShouldCreateBook() {
        BookCreationRequest request = new BookCreationRequest(title, author, status.getValue(), Set.of(1, 2));
        BookCreation data = new BookCreation(title, author, null, Set.of(1, 2));
        Book book = new Book(
            id,
            title,
            null,
            createdAt,
            Collections.emptySet(),
            Set.of(),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            List.of(),
            List.of()
        );
        BookResponse bookResponse = new BookResponse(
            id,
            title,
            null,
            status.getValue(),
            createdAt,
            Collections.emptySet(),
            Set.of(),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            List.of(),
            List.of()
        );
        when(bookWebMapper.toDomain(request)).thenReturn(data);
        when(createBookUseCase.execute(data, userId)).thenReturn(book);
        when(bookWebMapper.toResponse(book)).thenReturn(bookResponse);

        var mockResponse = mockMvcTester.post().uri("/api/v1/books")
            .with(authentication(getAuth()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request));

        assertThat(mockResponse)
            .hasStatus(HttpStatus.CREATED)
            .bodyJson()
            .convertTo(BookResponse.class)
            .satisfies(response -> {
                    assertThat(response.id()).isEqualTo(bookResponse.id());
                    assertThat(response.title()).isEqualTo(bookResponse.title());
                }
            );
    }

    @Test
    void uploadCover_ShouldUploadCover() {
        String coverFileName = "cover.jpg";
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
            "cover",
            "image.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "content".getBytes()
        );
        UploadCover data = new UploadCover(MediaType.IMAGE_JPEG_VALUE, null, 0L);
        Book book = new Book(
            id,
            title,
            coverFileName,
            createdAt,
            null,
            Set.of(),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            List.of(),
            List.of()
        );
        BookResponse bookResponse = new BookResponse(
            id,
            title,
            coverFileName,
            status.getValue(),
            createdAt,
            null,
            Set.of(),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            List.of(),
            List.of()
        );
        when(uploadCoverWebMapper.toDomain(mockMultipartFile)).thenReturn(data);
        when(uploadCoverUseCase.execute(id, userId, data)).thenReturn(book);
        when(bookWebMapper.toResponse(book)).thenReturn(bookResponse);

        var mockResponse = mockMvcTester.post().uri("/api/v1/books/" + id + "/cover")
            .with(authentication(getAuth()))
            .multipart()
            .file(mockMultipartFile);

        assertThat(mockResponse)
            .hasStatus(HttpStatus.OK)
            .bodyJson()
            .convertTo(BookResponse.class)
            .satisfies(response -> assertThat(response.coverUrl()).isEqualTo(bookResponse.coverUrl()));
    }

    @Test
    void deleteCover_ShouldReturnNoContent() {
        var mockResponse = mockMvcTester.delete().uri("/api/v1/books/" + id + "/cover")
            .with(authentication(getAuth()));

        assertThat(mockResponse).hasStatus(HttpStatus.NO_CONTENT);
        verify(deleteCoverUseCase).execute(id, userId);
    }

    @Test
    void getCover_ShouldReturnImageFile() {
        byte[] content = "content".getBytes();
        String coverFileName = "cover.webp";
        String contentType = "image/webp";
        ImageFile imageFile = new ImageFile(
            coverFileName,
            contentType,
            new ByteArrayInputStream(content),
            content.length
        );
        when(getBookCoverUseCase.execute(id, userId)).thenReturn(imageFile);

        var mockResponse = mockMvcTester.get().uri("/api/v1/books/" + id + "/cover")
            .with(authentication(getAuth()));

        assertThat(mockResponse)
            .hasStatus(HttpStatus.OK)
            .hasContentType(MediaType.parseMediaType(contentType))
            .hasHeader(HttpHeaders.CONTENT_DISPOSITION, "filename=\"" + coverFileName + "\"")
            .hasHeader(HttpHeaders.CACHE_CONTROL, "max-age=86400")
            .body().isEqualTo(content);
    }
}