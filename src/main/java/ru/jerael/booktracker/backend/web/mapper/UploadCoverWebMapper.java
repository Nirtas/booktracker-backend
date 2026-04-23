package ru.jerael.booktracker.backend.web.mapper;

import org.springframework.web.multipart.MultipartFile;
import ru.jerael.booktracker.backend.domain.model.book.UploadCoverPayload;
import ru.jerael.booktracker.backend.web.annotation.WebMapper;
import ru.jerael.booktracker.backend.web.exception.factory.FileWebExceptionFactory;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

@WebMapper
public class UploadCoverWebMapper {
    public UploadCoverPayload toDomain(MultipartFile file, UUID bookId, UUID userId) {
        if (file == null) return null;

        try {
            InputStream inputStream = new BufferedInputStream(file.getInputStream());
            return new UploadCoverPayload(
                bookId,
                userId,
                file.getContentType(),
                inputStream,
                file.getSize()
            );
        } catch (IOException e) {
            String fileName = Objects.requireNonNull(file.getOriginalFilename(), "unknown file name");
            throw FileWebExceptionFactory.readError(fileName, e);
        }
    }
}
