package ru.jerael.booktracker.backend.domain.model.image;

import java.io.InputStream;

public record ProcessedImage(
    String contentType,
    String extension,
    InputStream content,
    long size
) {}
