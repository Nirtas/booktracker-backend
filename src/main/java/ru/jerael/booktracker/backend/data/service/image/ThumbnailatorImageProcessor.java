package ru.jerael.booktracker.backend.data.service.image;

import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import ru.jerael.booktracker.backend.data.exception.factory.ImageProcessingExceptionFactory;
import ru.jerael.booktracker.backend.data.service.image.config.ImageProperties;
import ru.jerael.booktracker.backend.domain.constant.ImageRules;
import ru.jerael.booktracker.backend.domain.model.image.ImageFormat;
import ru.jerael.booktracker.backend.domain.model.image.ProcessedImage;
import ru.jerael.booktracker.backend.domain.service.image.ImageProcessor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class ThumbnailatorImageProcessor implements ImageProcessor {
    private final ImageProperties imageProperties;

    @Override
    public ProcessedImage process(InputStream content) {
        ImageFormat format = imageProperties.getTargetFormat();
        try (content; ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Thumbnails.of(content)
                .size(imageProperties.getMaxWidth(), imageProperties.getMaxHeight())
                .outputFormat(format.getExtension())
                .outputQuality(imageProperties.getQuality())
                .toOutputStream(outputStream);
            byte[] bytes = outputStream.toByteArray();
            return new ProcessedImage(
                ImageRules.FORMAT_TO_MIME.get(format),
                format.getExtension(),
                new ByteArrayInputStream(bytes),
                bytes.length
            );
        } catch (Exception e) {
            throw ImageProcessingExceptionFactory.failedToProcess(e.getMessage(), e);
        }
    }
}
