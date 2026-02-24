package ru.jerael.booktracker.backend.api.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.jerael.booktracker.backend.data.exception.factory.StorageExceptionFactory;
import ru.jerael.booktracker.backend.domain.exception.InternalException;
import ru.jerael.booktracker.backend.domain.model.book.UploadCover;
import java.io.IOException;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UploadCoverApiMapperTest {
    private final UploadCoverApiMapper uploadCoverApiMapper = new UploadCoverApiMapper();

    private final UUID id = UUID.fromString("ee39af7a-a073-4473-878a-1aae34e98bb7");

    @Test
    void toDomain_ShouldMap() throws IOException {
        String fileName = "image.jpg";
        String contentType = MediaType.IMAGE_JPEG_VALUE;
        byte[] content = "content".getBytes();
        MockMultipartFile mockMultipartFile = new MockMultipartFile("cover", fileName, contentType, content);

        UploadCover data = uploadCoverApiMapper.toDomain(id, mockMultipartFile);

        assertEquals(id, data.bookId());
        assertEquals(contentType, data.contentType());
        assertThat(data.content().readAllBytes()).isEqualTo(content);
    }

    @Test
    void toDomain_WhenIOExceptionOccurs_ShouldThrowStorageError() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenThrow(StorageExceptionFactory.error("details", null));

        assertThrows(InternalException.class, () -> uploadCoverApiMapper.toDomain(id, file));
    }
}