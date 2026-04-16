package ru.jerael.booktracker.backend.domain.model.book;

import java.io.InputStream;
import java.util.UUID;

public record UploadCoverPayload(
    UUID bookId,
    UUID userId,
    String contentType,
    InputStream content,
    long size
) {}
