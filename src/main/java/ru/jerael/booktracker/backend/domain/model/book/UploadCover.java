package ru.jerael.booktracker.backend.domain.model.book;

import java.io.InputStream;
import java.util.UUID;

public record UploadCover(
    UUID bookId,
    String contentType,
    InputStream content
) {}
