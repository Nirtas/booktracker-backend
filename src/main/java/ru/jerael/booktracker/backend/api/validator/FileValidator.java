package ru.jerael.booktracker.backend.api.validator;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.jerael.booktracker.backend.domain.exception.factory.FileValidationExceptionFactory;

@Component
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
