package ru.jerael.booktracker.backend.domain.constant;

import java.util.Map;
import java.util.Set;

public final class BookRules {
    private BookRules() {}

    public static final int TITLE_MAX_LENGTH = 500;
    public static final int AUTHOR_MAX_LENGTH = 500;

    public static final Set<String> ALLOWED_IMAGE_MIME_TYPES =
        Set.of("image/jpeg", "image/png", "image/webp", "image/bmp");
    public static final Map<String, String> MIME_TO_EXTENSION = Map.of(
        "image/jpeg", "jpg",
        "image/png", "png",
        "image/webp", "webp",
        "image/bmp", "bmp"
    );
}
