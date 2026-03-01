package ru.jerael.booktracker.backend.domain.constant;

import java.util.Map;
import java.util.Set;

public final class ImageRules {
    private ImageRules() {}

    public static final Set<String> ALLOWED_MIME_TYPES = Set.of("image/jpeg", "image/png", "image/webp", "image/bmp");

    public static final Map<String, String> MIME_TO_EXTENSION = Map.of(
        "image/jpeg", "jpg",
        "image/png", "png",
        "image/webp", "webp",
        "image/bmp", "bmp"
    );
}
