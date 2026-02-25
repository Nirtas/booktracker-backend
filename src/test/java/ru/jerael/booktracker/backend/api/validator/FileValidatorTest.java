package ru.jerael.booktracker.backend.api.validator;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import ru.jerael.booktracker.backend.domain.exception.ValidationException;
import ru.jerael.booktracker.backend.domain.exception.code.ErrorCode;
import static org.junit.jupiter.api.Assertions.*;

class FileValidatorTest {
    private final FileValidator fileValidator = new FileValidator();

    private final String fieldName = "cover";
    private final String fileName = "image.jpg";
    private final String contentType = MediaType.IMAGE_JPEG_VALUE;
    private final byte[] content = "content".getBytes();

    @Test
    void validate_ShouldPassValidationForValidFile() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("cover", fileName, contentType, content);

        assertDoesNotThrow(() -> fileValidator.validate(mockMultipartFile, fieldName));
    }

    @Test
    void validate_WhenFileIsNull_ShouldThrowValidationException() {
        ErrorCode errorCode =
            assertThrows(ValidationException.class, () -> fileValidator.validate(null, fieldName)).getErrorCode();

        assertEquals("EMPTY_FILE_CONTENT", errorCode.name());
    }

    @Test
    void validate_WhenFileIsEmpty_ShouldThrowValidationException() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("cover", fileName, contentType, new byte[0]);
        ErrorCode errorCode = assertThrows(ValidationException.class,
            () -> fileValidator.validate(mockMultipartFile, fieldName)).getErrorCode();

        assertEquals("EMPTY_FILE_CONTENT", errorCode.name());
    }

    @Test
    void validate_WhenFileNameIsNull_ShouldThrowValidationException() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("cover", null, contentType, content);
        ErrorCode errorCode = assertThrows(ValidationException.class,
            () -> fileValidator.validate(mockMultipartFile, fieldName)).getErrorCode();

        assertEquals("EMPTY_FILE_NAME", errorCode.name());
    }

    @Test
    void validate_WhenFileNameIsBlank_ShouldThrowValidationException() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("cover", "   ", contentType, content);
        ErrorCode errorCode = assertThrows(ValidationException.class,
            () -> fileValidator.validate(mockMultipartFile, fieldName)).getErrorCode();

        assertEquals("EMPTY_FILE_NAME", errorCode.name());
    }
}