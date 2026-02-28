package ru.jerael.booktracker.backend.domain.model.book;

import java.io.InputStream;

public record UploadCover(
    String contentType,
    InputStream content,
    long size
) {}
