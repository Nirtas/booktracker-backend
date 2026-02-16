package ru.jerael.booktracker.backend.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;
import ru.jerael.booktracker.backend.api.dto.book.BookResponse;
import ru.jerael.booktracker.backend.api.handler.GlobalExceptionHandler;
import ru.jerael.booktracker.backend.api.mapper.BookApiMapper;
import ru.jerael.booktracker.backend.api.mapper.GenreApiMapper;
import ru.jerael.booktracker.backend.domain.exception.NotFoundException;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.BookStatus;
import ru.jerael.booktracker.backend.domain.usecase.book.GetBookByIdUseCase;
import ru.jerael.booktracker.backend.domain.usecase.book.GetBooksUseCase;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(BookController.class)
@Import({GlobalExceptionHandler.class, BookApiMapper.class, GenreApiMapper.class})
@AutoConfigureRestTestClient
class BookControllerTest {

    @Autowired
    private RestTestClient restTestClient;

    @MockitoBean
    private GetBooksUseCase getBooksUseCase;

    @MockitoBean
    private GetBookByIdUseCase getBookByIdUseCase;

    @Test
    void getAll_ShouldReturnSetOfBooks() {
        UUID id = UUID.fromString("ee39af7a-a073-4473-878a-1aae34e98bb7");
        String title = "title";
        String author = "author";
        BookStatus status = BookStatus.READING;
        Instant createdAt = Instant.ofEpochMilli(1771249699347L);
        Book book = new Book(id, title, author, null, status, createdAt, Collections.emptySet());
        BookResponse bookResponse = new BookResponse(id, title, author, null, status.getValue(),
            createdAt.toEpochMilli(), Collections.emptySet());
        when(getBooksUseCase.execute()).thenReturn(List.of(book));

        restTestClient.get().uri("/api/v1/books")
            .exchange()
            .expectStatus().isOk()
            .expectBody(new ParameterizedTypeReference<List<BookResponse>>() {})
            .isEqualTo(List.of(bookResponse));

        verify(getBooksUseCase).execute();
    }

    @Test
    void getById_WhenExceptionThrown_ShouldReturnNotFound() {
        UUID id = UUID.fromString("31d3f5e3-7faf-4678-a3cf-4657d8875a82");
        when(getBookByIdUseCase.execute(id)).thenThrow(NotFoundException.bookNotFound(id));

        restTestClient.get().uri("/api/v1/books/" + id)
            .exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("$.message").isEqualTo("Book with id " + id + " was not found");

        verify(getBookByIdUseCase).execute(id);
    }
}