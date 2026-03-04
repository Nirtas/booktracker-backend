package ru.jerael.booktracker.backend.domain.model.image;

import java.io.InputStream;

public record ImageFile(
    String fileName,
    String contentType,
    InputStream content,
    long size
) {}
