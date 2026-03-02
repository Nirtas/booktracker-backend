package ru.jerael.booktracker.backend.domain.constant;

import ru.jerael.booktracker.backend.domain.model.image.ImageFormat;
import java.util.Map;
import java.util.Set;

public final class ImageRules {
    private ImageRules() {}

    public static final Set<String> ALLOWED_MIME_TYPES = Set.of("image/jpeg", "image/png", "image/webp", "image/bmp");

    public static final Map<ImageFormat, String> FORMAT_TO_MIME = Map.of(
        ImageFormat.JPG, "image/jpeg",
        ImageFormat.JPEG, "image/jpeg",
        ImageFormat.PNG, "image/png",
        ImageFormat.WEBP, "image/webp",
        ImageFormat.BMP, "image/bmp"
    );
}
