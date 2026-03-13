package ru.jerael.booktracker.backend.web.exception.handler;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.NoHandlerFoundException;
import ru.jerael.booktracker.backend.domain.exception.AppException;
import ru.jerael.booktracker.backend.domain.exception.code.*;
import ru.jerael.booktracker.backend.domain.exception.factory.*;
import ru.jerael.booktracker.backend.web.config.WebProperties;
import ru.jerael.booktracker.backend.web.exception.code.WebErrorCode;
import ru.jerael.booktracker.backend.web.exception.factory.FileWebExceptionFactory;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@WebMvcTest(GlobalExceptionHandlerTest.TestController.class)
@Import(GlobalExceptionHandlerTest.TestController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private WebProperties webProperties;

    private static final UUID id = UUID.fromString("ee39af7a-a073-4473-878a-1aae34e98bb7");

    record TestDto(@NotBlank String name) {}

    @RestController
    static class TestController {

        @GetMapping("/test/not-found")
        void notFound() {
            throw BookExceptionFactory.bookNotFound(id);
        }

        @GetMapping("/test/too-many-requests")
        void tooManyRequests() {
            throw EmailVerificationExceptionFactory.tooManyRequests();
        }

        @GetMapping("/test/unauthenticated")
        void unauthenticated() {
            throw IdentityTokenExceptionFactory.invalidSignature();
        }

        @GetMapping("/test/already-exists")
        void alreadyExists() {
            throw UserExceptionFactory.emailAlreadyExists("test@example.com");
        }

        @GetMapping("/test/validation")
        void validation() {
            throw FileValidationExceptionFactory.emptyFileName("cover");
        }

        @GetMapping("/test/internal")
        void internal() {
            throw FileWebExceptionFactory.readError("cover.jpg", null);
        }

        @GetMapping("/test/app")
        void app() {
            throw new AppException(CommonErrorCode.VALIDATION_ERROR, "App error") {};
        }

        @GetMapping("/test/missing-token")
        void insufficientAuthentication() {
            throw new InsufficientAuthenticationException("Missing token");
        }

        @GetMapping("/test/type-mismatch/{id}")
        void testTypeMismatch(@PathVariable UUID id) {}

        @PostMapping("/test/max-upload-size-exceeded")
        void maxUploadSizeExceeded() {
            throw new MaxUploadSizeExceededException(1L);
        }

        @PostMapping("/test/missing-servlet-request-part")
        void missingServletRequestPart(@RequestPart("cover") MultipartFile cover) {}

        @GetMapping("/test/multipart")
        void multipart() {
            throw new MultipartException("Multipart error");
        }

        @PostMapping("/test/method-argument-not-valid")
        void methodArgumentNotValid(@Valid @RequestBody TestDto dto) {}

        @PostMapping("/test/http-message-not-readable")
        void httpMessageNotReadable(@RequestBody TestDto dto) {}

        @GetMapping("/test/no-handler-found")
        void noHandlerFound() throws NoHandlerFoundException {
            throw new NoHandlerFoundException("GET", "/test/no-handler-found", HttpHeaders.EMPTY);
        }

        @GetMapping("/test/general")
        void general() {
            throw new RuntimeException("Unexpected error");
        }
    }

    @Test
    void handleNotFoundException() {
        var response = mockMvcTester.get().uri("/test/not-found");
        assertThat(response).hasStatus(HttpStatus.NOT_FOUND);
        var json = assertThat(response).bodyJson();
        json.extractingPath("$.detail").isEqualTo("Book with id " + id + " was not found");
        json.extractingPath("$.title").isEqualTo("Resource not found");
        json.extractingPath("$.code").isEqualTo(BookErrorCode.BOOK_NOT_FOUND.name());
    }

    @Test
    void handleTooManyRequestsException() {
        var response = mockMvcTester.get().uri("/test/too-many-requests");
        assertThat(response).hasStatus(HttpStatus.TOO_MANY_REQUESTS);
        var json = assertThat(response).bodyJson();
        json.extractingPath("$.detail").isEqualTo("Please wait before requesting another code");
        json.extractingPath("$.title").isEqualTo("Rate limit exceeded");
        json.extractingPath("$.code").isEqualTo(EmailVerificationErrorCode.TOO_MANY_REQUESTS.name());
    }

    @Test
    void handleUnauthenticatedException() {
        var response = mockMvcTester.get().uri("/test/unauthenticated");
        assertThat(response).hasStatus(HttpStatus.UNAUTHORIZED);
        var json = assertThat(response).bodyJson();
        json.extractingPath("$.detail").isEqualTo("Token signature is invalid");
        json.extractingPath("$.title").isEqualTo("Authentication failed");
        json.extractingPath("$.code").isEqualTo(IdentityTokenErrorCode.INVALID_SIGNATURE.name());
    }

    @Test
    void handleAlreadyExistsException() {
        var response = mockMvcTester.get().uri("/test/already-exists");
        assertThat(response).hasStatus(HttpStatus.CONFLICT);
        var json = assertThat(response).bodyJson();
        json.extractingPath("$.detail").isEqualTo("User with email test@example.com already exists");
        json.extractingPath("$.title").isEqualTo("Already exists");
        json.extractingPath("$.code").isEqualTo(UserErrorCode.EMAIL_ALREADY_EXISTS.name());
    }

    @Test
    void handleValidationException() {
        var response = mockMvcTester.get().uri("/test/validation");
        assertThat(response).hasStatus(HttpStatus.BAD_REQUEST);
        var json = assertThat(response).bodyJson();
        json.extractingPath("$.detail").isEqualTo("Request contains invalid data");
        json.extractingPath("$.title").isEqualTo("Validation failed");
        json.extractingPath("$.code").isEqualTo(CommonErrorCode.VALIDATION_ERROR.name());
    }

    @Test
    void handleInternalException() {
        var response = mockMvcTester.get().uri("/test/internal");
        assertThat(response).hasStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        var json = assertThat(response).bodyJson();
        json.extractingPath("$.detail").isEqualTo("An internal server error occurred");
        json.extractingPath("$.title").isEqualTo("Internal server error");
        json.extractingPath("$.code").isEqualTo(WebErrorCode.INTERNAL_SERVER_ERROR.name());
    }

    @Test
    void handleAppException() {
        var response = mockMvcTester.get().uri("/test/app");
        assertThat(response).hasStatus(HttpStatus.BAD_REQUEST);
        var json = assertThat(response).bodyJson();
        json.extractingPath("$.detail").isEqualTo("App error");
        json.extractingPath("$.title").isEqualTo("Application error");
        json.extractingPath("$.code").isEqualTo(CommonErrorCode.VALIDATION_ERROR.name());
    }

    @Test
    void handleInsufficientAuthenticationException() {
        var response = mockMvcTester.get().uri("/test/missing-token");
        assertThat(response).hasStatus(HttpStatus.UNAUTHORIZED);
        var json = assertThat(response).bodyJson();
        json.extractingPath("$.detail").isEqualTo("Missing token");
        json.extractingPath("$.title").isEqualTo("Authentication required");
        json.extractingPath("$.code").isEqualTo(WebErrorCode.MISSING_TOKEN.name());
    }

    @Test
    void handleHttpRequestMethodNotSupportedException() {
        var response = mockMvcTester.post().uri("/test/app");
        assertThat(response).hasStatus(HttpStatus.METHOD_NOT_ALLOWED);
        var json = assertThat(response).bodyJson();
        json.extractingPath("$.detail").isEqualTo("Method POST is not supported for this endpoint");
        json.extractingPath("$.title").isEqualTo("Method not allowed");
        json.extractingPath("$.code").isEqualTo(WebErrorCode.METHOD_NOT_ALLOWED.name());
    }

    @Test
    void handleTypeMismatchException() {
        var response = mockMvcTester.get().uri("/test/type-mismatch/123");
        assertThat(response).hasStatus(HttpStatus.BAD_REQUEST);
        var json = assertThat(response).bodyJson();
        json.extractingPath("$.detail").isEqualTo("Parameter 'id' should be of type 'UUID'");
        json.extractingPath("$.title").isEqualTo("Type mismatch");
        json.extractingPath("$.code").isEqualTo(WebErrorCode.INVALID_ARGUMENT_TYPE.name());
    }

    @Test
    void handleMaxUploadSizeExceededException() {
        MockMultipartFile file = new MockMultipartFile(
            "cover",
            "image.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "content".getBytes()
        );
        DataSize limit = DataSize.ofMegabytes(10);
        when(webProperties.getMaxFileSize()).thenReturn(limit);
        String maxFileSize = "10MB";

        var response = mockMvcTester.post().uri("/test/max-upload-size-exceeded").multipart().file(file);

        assertThat(response).hasStatus(HttpStatus.BAD_REQUEST);
        var json = assertThat(response).bodyJson();
        json.extractingPath("$.detail").isEqualTo("File size exceeds the limit of " + maxFileSize);
        json.extractingPath("$.title").isEqualTo("File too large");
        json.extractingPath("$.code").isEqualTo(WebErrorCode.FILE_SIZE_EXCEEDED.name());
        json.extractingPath("$.params.max").isEqualTo(maxFileSize);
    }

    @Test
    void handleMissingServletRequestPartException() {
        var response = mockMvcTester.post().uri("/test/missing-servlet-request-part").multipart();
        assertThat(response).hasStatus(HttpStatus.BAD_REQUEST);
        var json = assertThat(response).bodyJson();
        json.extractingPath("$.detail").isEqualTo("Required part 'cover' is not present.");
        json.extractingPath("$.title").isEqualTo("Missing request part");
        json.extractingPath("$.code").isEqualTo(WebErrorCode.INVALID_MULTIPART_REQUEST.name());
    }

    @Test
    void handleMultipartException() {
        var response = mockMvcTester.get().uri("/test/multipart");
        assertThat(response).hasStatus(HttpStatus.BAD_REQUEST);
        var json = assertThat(response).bodyJson();
        json.extractingPath("$.detail").isEqualTo("Multipart error");
        json.extractingPath("$.title").isEqualTo("Multipart request error");
        json.extractingPath("$.code").isEqualTo(WebErrorCode.INVALID_MULTIPART_REQUEST.name());
    }

    @Test
    void handleMethodArgumentNotValidException() {
        var response = mockMvcTester.post().uri("/test/method-argument-not-valid")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}");
        assertThat(response).hasStatus(HttpStatus.BAD_REQUEST);
        var json = assertThat(response).bodyJson();
        json.extractingPath("$.detail").isEqualTo("Request body validation failed");
        json.extractingPath("$.title").isEqualTo("Constraint violation");
        json.extractingPath("$.code").isEqualTo(CommonErrorCode.VALIDATION_ERROR.name());
        json.extractingPath("$.errors[0].field").isEqualTo("name");
        json.extractingPath("$.errors[0].code").isEqualTo("NOT_BLANK");
    }

    @Test
    void handleHttpMessageNotReadableException() {
        var response = mockMvcTester.post().uri("/test/http-message-not-readable")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{invalid json}");
        assertThat(response).hasStatus(HttpStatus.BAD_REQUEST);
        var json = assertThat(response).bodyJson();
        json.extractingPath("$.detail").isEqualTo("Invalid JSON format or data types");
        json.extractingPath("$.title").isEqualTo("Malformed request");
        json.extractingPath("$.code").isEqualTo(WebErrorCode.MALFORMED_REQUEST.name());
    }

    @Test
    void handleNoHandlerFoundException() {
        var response = mockMvcTester.get().uri("/test/no-handler-found");
        assertThat(response).hasStatus(HttpStatus.NOT_FOUND);
        var json = assertThat(response).bodyJson();
        json.extractingPath("$.detail").isEqualTo("The requested endpoint /test/no-handler-found does not exist");
        json.extractingPath("$.title").isEqualTo("Endpoint not found");
        json.extractingPath("$.code").isEqualTo(WebErrorCode.ENDPOINT_NOT_FOUND.name());
    }

    @Test
    void handleGeneralException() {
        var response = mockMvcTester.get().uri("/test/general");
        assertThat(response).hasStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        var json = assertThat(response).bodyJson();
        json.extractingPath("$.detail").isEqualTo("An internal server error occurred");
        json.extractingPath("$.title").isEqualTo("Internal server error");
        json.extractingPath("$.code").isEqualTo(WebErrorCode.INTERNAL_SERVER_ERROR.name());
    }
}