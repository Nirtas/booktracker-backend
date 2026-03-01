package ru.jerael.booktracker.backend.data.storage;

import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.jerael.booktracker.backend.data.exception.factory.StorageExceptionFactory;
import ru.jerael.booktracker.backend.data.storage.config.MinioProperties;
import ru.jerael.booktracker.backend.domain.constant.ImageRules;
import ru.jerael.booktracker.backend.domain.model.book.UploadCover;
import ru.jerael.booktracker.backend.domain.storage.BookCoverStorage;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@Primary
@RequiredArgsConstructor
public class MinioBookCoverStorage implements BookCoverStorage {
    private final MinioProperties minioProperties;
    private final MinioClient minioClient;
    private String bucket;

    @PostConstruct
    private void init() {
        try {
            bucket = minioProperties.getBuckets().getCovers();
            boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder()
                    .bucket(bucket)
                    .build()
            );
            if (!exists) {
                minioClient.makeBucket(
                    MakeBucketArgs.builder()
                        .bucket(bucket)
                        .build()
                );
            }
        } catch (Exception e) {
            throw StorageExceptionFactory.error(e.getMessage(), e);
        }
    }

    @Override
    public String save(UUID bookId, UploadCover data) {
        try (InputStream content = data.content()) {
            String extension = ImageRules.MIME_TO_EXTENSION.get(data.contentType());
            String fileName = String.format("%s.%s", bookId, extension);
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucket)
                    .stream(content, data.size(), -1)
                    .contentType(data.contentType())
                    .object(fileName)
                    .build()
            );
            return fileName;
        } catch (Exception e) {
            throw StorageExceptionFactory.error(e.getMessage(), e);
        }
    }

    @Override
    public void delete(String fileName) {
        try {
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(fileName)
                    .build()
            );
        } catch (Exception e) {
            throw StorageExceptionFactory.error(e.getMessage(), e);
        }
    }

    @Override
    public String getUrl(String fileName) {
        if (fileName == null || fileName.isBlank()) return null;

        try {
            return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucket)
                    .object(fileName)
                    .expiry((int) minioProperties.getUrlExpiry().toSeconds(), TimeUnit.SECONDS)
                    .build()
            );
        } catch (Exception e) {
            throw StorageExceptionFactory.error(e.getMessage(), e);
        }
    }
}
