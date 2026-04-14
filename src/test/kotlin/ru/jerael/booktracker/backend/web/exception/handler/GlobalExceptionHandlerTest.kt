package ru.jerael.booktracker.backend.web.exception.handler

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.test.web.servlet.assertj.MockMvcTester
import org.springframework.util.unit.DataSize
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.multipart.MultipartException
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.NoHandlerFoundException
import ru.jerael.booktracker.backend.domain.exception.AppException
import ru.jerael.booktracker.backend.domain.exception.code.*
import ru.jerael.booktracker.backend.domain.exception.factory.*
import ru.jerael.booktracker.backend.domain.model.book.BookStatus
import ru.jerael.booktracker.backend.factory.file.FileFactory
import ru.jerael.booktracker.backend.web.config.WebProperties
import ru.jerael.booktracker.backend.web.exception.code.WebErrorCode
import ru.jerael.booktracker.backend.web.exception.factory.FileWebExceptionFactory
import java.util.*

@WebMvcTest(GlobalExceptionHandlerTest.TestController::class)
@Import(GlobalExceptionHandlerTest.TestController::class)
class GlobalExceptionHandlerTest {
    
    @Autowired
    private lateinit var mockMvcTester: MockMvcTester
    
    @MockkBean
    private lateinit var webProperties: WebProperties
    
    companion object {
        private val id: UUID = UUID.fromString("ee39af7a-a073-4473-878a-1aae34e98bb7")
    }
    
    data class TestDto(@field:NotBlank val name: String?)
    
    @RestController
    class TestController {
        
        @GetMapping("/test/not-found")
        fun notFound() {
            throw BookExceptionFactory.bookNotFound(id)
        }
        
        @GetMapping("/test/unprocessable-content")
        fun unprocessableContent() {
            throw BookExceptionFactory.invalidStatusTransition(BookStatus.COMPLETED, BookStatus.DROPPED)
        }
        
        @GetMapping("/test/too-many-requests")
        fun tooManyRequests() {
            throw EmailVerificationExceptionFactory.tooManyRequests()
        }
        
        @GetMapping("/test/unauthenticated")
        fun unauthenticated() {
            throw IdentityTokenExceptionFactory.invalidSignature()
        }
        
        @GetMapping("/test/already-exists")
        fun alreadyExists() {
            throw UserExceptionFactory.emailAlreadyExists("test@example.com")
        }
        
        @GetMapping("/test/validation")
        fun validation() {
            throw FileValidationExceptionFactory.emptyFileName("cover")
        }
        
        @GetMapping("/test/internal")
        fun internal() {
            throw FileWebExceptionFactory.readError("cover.jpg", null)
        }
        
        @GetMapping("/test/app")
        fun app() {
            throw object : AppException(CommonErrorCode.VALIDATION_ERROR, "App error") {}
        }
        
        @GetMapping("/test/missing-token")
        fun insufficientAuthentication() {
            throw InsufficientAuthenticationException("Missing token")
        }
        
        @GetMapping("/test/type-mismatch/{id}")
        fun testTypeMismatch(@PathVariable id: UUID?) {
        }
        
        @PostMapping("/test/max-upload-size-exceeded")
        fun maxUploadSizeExceeded() {
            throw MaxUploadSizeExceededException(1L)
        }
        
        @PostMapping("/test/missing-servlet-request-part")
        fun missingServletRequestPart(@RequestPart("cover") cover: MultipartFile) {
        }
        
        @GetMapping("/test/multipart")
        fun multipart() {
            throw MultipartException("Multipart error")
        }
        
        @PostMapping("/test/method-argument-not-valid")
        fun methodArgumentNotValid(@Valid @RequestBody dto: TestDto) {
        }
        
        @PostMapping("/test/http-message-not-readable")
        fun httpMessageNotReadable(@RequestBody dto: TestDto) {
        }
        
        @GetMapping("/test/no-handler-found")
        fun noHandlerFound() {
            throw NoHandlerFoundException("GET", "/test/no-handler-found", HttpHeaders.EMPTY)
        }
        
        @GetMapping("/test/general")
        fun general() {
            throw RuntimeException("Unexpected error")
        }
    }
    
    @Test
    fun handleNotFoundException() {
        val response = mockMvcTester.get().uri("/test/not-found")
        assertThat(response).hasStatus(HttpStatus.NOT_FOUND)
        val json = assertThat(response).bodyJson()
        json.extractingPath("$.detail").isEqualTo("Book with id $id was not found")
        json.extractingPath("$.title").isEqualTo("Resource not found")
        json.extractingPath("$.code").isEqualTo(BookErrorCode.BOOK_NOT_FOUND.name)
    }
    
    @Test
    fun handleUnprocessableContentException() {
        val response = mockMvcTester.get().uri("/test/unprocessable-content")
        assertThat(response).hasStatus(HttpStatus.UNPROCESSABLE_CONTENT)
        val json = assertThat(response).bodyJson()
        json.extractingPath("$.detail")
            .isEqualTo("Transition from ${BookStatus.COMPLETED} to ${BookStatus.DROPPED} is not allowed")
        json.extractingPath("$.title").isEqualTo("Business rule violation")
        json.extractingPath("$.code").isEqualTo(BookErrorCode.INVALID_BOOK_STATUS_TRANSITION.name)
    }
    
    @Test
    fun handleTooManyRequestsException() {
        val response = mockMvcTester.get().uri("/test/too-many-requests")
        assertThat(response).hasStatus(HttpStatus.TOO_MANY_REQUESTS)
        val json = assertThat(response).bodyJson()
        json.extractingPath("$.detail").isEqualTo("Please wait before requesting another code")
        json.extractingPath("$.title").isEqualTo("Rate limit exceeded")
        json.extractingPath("$.code").isEqualTo(EmailVerificationErrorCode.TOO_MANY_REQUESTS.name)
    }
    
    @Test
    fun handleUnauthenticatedException() {
        val response = mockMvcTester.get().uri("/test/unauthenticated")
        assertThat(response).hasStatus(HttpStatus.UNAUTHORIZED)
        val json = assertThat(response).bodyJson()
        json.extractingPath("$.detail").isEqualTo("Token signature is invalid")
        json.extractingPath("$.title").isEqualTo("Authentication failed")
        json.extractingPath("$.code").isEqualTo(IdentityTokenErrorCode.INVALID_SIGNATURE.name)
    }
    
    @Test
    fun handleAlreadyExistsException() {
        val response = mockMvcTester.get().uri("/test/already-exists")
        assertThat(response).hasStatus(HttpStatus.CONFLICT)
        val json = assertThat(response).bodyJson()
        json.extractingPath("$.detail").isEqualTo("User with email test@example.com already exists")
        json.extractingPath("$.title").isEqualTo("Already exists")
        json.extractingPath("$.code").isEqualTo(UserErrorCode.EMAIL_ALREADY_EXISTS.name)
    }
    
    @Test
    fun handleValidationException() {
        val response = mockMvcTester.get().uri("/test/validation")
        assertThat(response).hasStatus(HttpStatus.BAD_REQUEST)
        val json = assertThat(response).bodyJson()
        json.extractingPath("$.detail").isEqualTo("Request contains invalid data")
        json.extractingPath("$.title").isEqualTo("Validation failed")
        json.extractingPath("$.code").isEqualTo(CommonErrorCode.VALIDATION_ERROR.name)
    }
    
    @Test
    fun handleInternalException() {
        val response = mockMvcTester.get().uri("/test/internal")
        assertThat(response).hasStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        val json = assertThat(response).bodyJson()
        json.extractingPath("$.detail").isEqualTo("An internal server error occurred")
        json.extractingPath("$.title").isEqualTo("Internal server error")
        json.extractingPath("$.code").isEqualTo(WebErrorCode.INTERNAL_SERVER_ERROR.name)
    }
    
    @Test
    fun handleAppException() {
        val response = mockMvcTester.get().uri("/test/app")
        assertThat(response).hasStatus(HttpStatus.BAD_REQUEST)
        val json = assertThat(response).bodyJson()
        json.extractingPath("$.detail").isEqualTo("App error")
        json.extractingPath("$.title").isEqualTo("Application error")
        json.extractingPath("$.code").isEqualTo(CommonErrorCode.VALIDATION_ERROR.name)
    }
    
    @Test
    fun handleInsufficientAuthenticationException() {
        val response = mockMvcTester.get().uri("/test/missing-token")
        assertThat(response).hasStatus(HttpStatus.UNAUTHORIZED)
        val json = assertThat(response).bodyJson()
        json.extractingPath("$.detail").isEqualTo("Missing token")
        json.extractingPath("$.title").isEqualTo("Authentication required")
        json.extractingPath("$.code").isEqualTo(WebErrorCode.MISSING_TOKEN.name)
    }
    
    @Test
    fun handleHttpRequestMethodNotSupportedException() {
        val response = mockMvcTester.post().uri("/test/app")
        assertThat(response).hasStatus(HttpStatus.METHOD_NOT_ALLOWED)
        val json = assertThat(response).bodyJson()
        json.extractingPath("$.detail").isEqualTo("Method POST is not supported for this endpoint")
        json.extractingPath("$.title").isEqualTo("Method not allowed")
        json.extractingPath("$.code").isEqualTo(WebErrorCode.METHOD_NOT_ALLOWED.name)
    }
    
    @Test
    fun handleTypeMismatchException() {
        val response = mockMvcTester.get().uri("/test/type-mismatch/123")
        assertThat(response).hasStatus(HttpStatus.BAD_REQUEST)
        val json = assertThat(response).bodyJson()
        json.extractingPath("$.detail").isEqualTo("Parameter 'id' should be of type 'UUID'")
        json.extractingPath("$.title").isEqualTo("Type mismatch")
        json.extractingPath("$.code").isEqualTo(WebErrorCode.INVALID_ARGUMENT_TYPE.name)
    }
    
    @Test
    fun handleMaxUploadSizeExceededException() {
        val file = FileFactory.createMockMultipartFile()
        val limit = DataSize.ofMegabytes(10)
        val maxFileSize = "10MB"
        every { webProperties.maxFileSize } returns limit
        
        val response = mockMvcTester.post().uri("/test/max-upload-size-exceeded").multipart().file(file)
        
        assertThat(response).hasStatus(HttpStatus.BAD_REQUEST)
        val json = assertThat(response).bodyJson()
        json.extractingPath("$.detail").isEqualTo("File size exceeds the limit of $maxFileSize")
        json.extractingPath("$.title").isEqualTo("File too large")
        json.extractingPath("$.code").isEqualTo(WebErrorCode.FILE_SIZE_EXCEEDED.name)
        json.extractingPath("$.params.max").isEqualTo(maxFileSize)
    }
    
    @Test
    fun handleMissingServletRequestPartException() {
        val response = mockMvcTester.post().uri("/test/missing-servlet-request-part").multipart()
        assertThat(response).hasStatus(HttpStatus.BAD_REQUEST)
        val json = assertThat(response).bodyJson()
        json.extractingPath("$.detail").isEqualTo("Required part 'cover' is not present.")
        json.extractingPath("$.title").isEqualTo("Missing request part")
        json.extractingPath("$.code").isEqualTo(WebErrorCode.INVALID_MULTIPART_REQUEST.name)
    }
    
    @Test
    fun handleMultipartException() {
        val response = mockMvcTester.get().uri("/test/multipart")
        assertThat(response).hasStatus(HttpStatus.BAD_REQUEST)
        val json = assertThat(response).bodyJson()
        json.extractingPath("$.detail").isEqualTo("Multipart error")
        json.extractingPath("$.title").isEqualTo("Multipart request error")
        json.extractingPath("$.code").isEqualTo(WebErrorCode.INVALID_MULTIPART_REQUEST.name)
    }
    
    @Test
    fun handleMethodArgumentNotValidException() {
        val response = mockMvcTester.post().uri("/test/method-argument-not-valid")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}")
        assertThat(response).hasStatus(HttpStatus.BAD_REQUEST)
        val json = assertThat(response).bodyJson()
        json.extractingPath("$.detail").isEqualTo("Request body validation failed")
        json.extractingPath("$.title").isEqualTo("Constraint violation")
        json.extractingPath("$.code").isEqualTo(CommonErrorCode.VALIDATION_ERROR.name)
        json.extractingPath("$.errors[0].field").isEqualTo("name")
        json.extractingPath("$.errors[0].code").isEqualTo("NOT_BLANK")
    }
    
    @Test
    fun handleHttpMessageNotReadableException() {
        val response = mockMvcTester.post().uri("/test/http-message-not-readable")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{invalid json}")
        assertThat(response).hasStatus(HttpStatus.BAD_REQUEST)
        val json = assertThat(response).bodyJson()
        json.extractingPath("$.detail").isEqualTo("Invalid JSON format or data types")
        json.extractingPath("$.title").isEqualTo("Malformed request")
        json.extractingPath("$.code").isEqualTo(WebErrorCode.MALFORMED_REQUEST.name)
    }
    
    @Test
    fun handleNoHandlerFoundException() {
        val response = mockMvcTester.get().uri("/test/no-handler-found")
        assertThat(response).hasStatus(HttpStatus.NOT_FOUND)
        val json = assertThat(response).bodyJson()
        json.extractingPath("$.detail").isEqualTo("The requested endpoint /test/no-handler-found does not exist")
        json.extractingPath("$.title").isEqualTo("Endpoint not found")
        json.extractingPath("$.code").isEqualTo(WebErrorCode.ENDPOINT_NOT_FOUND.name)
    }
    
    @Test
    fun handleGeneralException() {
        val response = mockMvcTester.get().uri("/test/general")
        assertThat(response).hasStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        val json = assertThat(response).bodyJson()
        json.extractingPath("$.detail").isEqualTo("An internal server error occurred")
        json.extractingPath("$.title").isEqualTo("Internal server error")
        json.extractingPath("$.code").isEqualTo(WebErrorCode.INTERNAL_SERVER_ERROR.name)
    }
}