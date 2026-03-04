package ru.jerael.booktracker.backend.data.storage;

import io.minio.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.jerael.booktracker.backend.data.exception.factory.StorageExceptionFactory;
import ru.jerael.booktracker.backend.data.storage.config.MinioProperties;
import ru.jerael.booktracker.backend.domain.model.image.ImageFile;
import ru.jerael.booktracker.backend.domain.storage.BookCoverStorage;
import java.io.InputStream;
import java.util.Objects;

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
    public void save(ImageFile data) {
        try (InputStream content = data.content()) {
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucket)
                    .stream(content, data.size(), -1)
                    .contentType(data.contentType())
                    .object(data.fileName())
                    .build()
            );
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
    public ImageFile download(String fileName) {
        try {
            GetObjectResponse response = minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(fileName)
                    .build()
            );
            return new ImageFile(
                fileName,
                response.headers().get("Content-Type"),
                response,
                Long.parseLong(Objects.requireNonNull(response.headers().get("Content-Length")))
            );
        } catch (Exception e) {
            throw StorageExceptionFactory.error(e.getMessage(), e);
        }
    }
}
