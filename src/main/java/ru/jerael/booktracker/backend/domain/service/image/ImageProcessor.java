package ru.jerael.booktracker.backend.domain.service.image;

import ru.jerael.booktracker.backend.domain.model.image.ProcessedImage;
import java.io.InputStream;

public interface ImageProcessor {
    ProcessedImage process(InputStream content);
}
