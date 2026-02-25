package ru.jerael.booktracker.backend.api.mapper;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.jerael.booktracker.backend.api.exception.factory.FileApiExceptionFactory;
import ru.jerael.booktracker.backend.domain.model.book.UploadCover;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

@Component
public class UploadCoverApiMapper {
    public UploadCover toDomain(UUID bookId, MultipartFile file) {
        if (bookId == null || file == null) return null;

        try {
            InputStream inputStream = new BufferedInputStream(file.getInputStream());
            return new UploadCover(
                bookId,
                file.getContentType(),
                inputStream
            );
        } catch (IOException e) {
            String fileName = Objects.requireNonNull(file.getOriginalFilename(), "unknown file name");
            throw FileApiExceptionFactory.readError(fileName, e);
        }
    }
}
