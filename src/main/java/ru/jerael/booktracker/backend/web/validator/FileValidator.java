package ru.jerael.booktracker.backend.web.validator;

import org.springframework.web.multipart.MultipartFile;
import ru.jerael.booktracker.backend.domain.exception.factory.FileValidationExceptionFactory;
import ru.jerael.booktracker.backend.web.annotation.WebValidator;

@WebValidator
public class FileValidator {
    public void validate(MultipartFile file, String fieldName) {
        if (file == null || file.isEmpty()) {
            throw FileValidationExceptionFactory.emptyFileContent(fieldName);
        }
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isBlank()) {
            throw FileValidationExceptionFactory.emptyFileName(fieldName);
        }
    }
}
