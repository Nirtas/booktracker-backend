package ru.jerael.booktracker.backend.domain.model.image;

import java.io.InputStream;

public record SaveImage(
    String fileName,
    String contentType,
    InputStream content,
    long size
) {}
