package ru.jerael.booktracker.backend.data.service.image;

import org.junit.jupiter.api.Test;
import ru.jerael.booktracker.backend.data.service.image.config.ImageProperties;
import ru.jerael.booktracker.backend.domain.exception.InternalException;
import ru.jerael.booktracker.backend.domain.model.image.ProcessedImage;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThumbnailatorImageProcessorTest {
    private final ImageProperties imageProperties = new ImageProperties();
    private final ThumbnailatorImageProcessor processor = new ThumbnailatorImageProcessor(imageProperties);

    @Test
    void process_ShouldConvertJpgToWebp() throws IOException {
        InputStream inputStream = createImage(100, 100, "jpg");

        ProcessedImage result = processor.process(inputStream);

        assertThat(result.contentType()).isEqualTo("image/webp");
        assertThat(result.size()).isGreaterThan(0);
    }

    @Test
    void process_ShouldResizeLargeImage() throws IOException {
        InputStream inputStream = createImage(3000, 1000, "jpg");
        imageProperties.setMaxWidth(1080);

        ProcessedImage result = processor.process(inputStream);

        BufferedImage outputImage = ImageIO.read(result.content());
        assertThat(outputImage.getWidth()).isEqualTo(imageProperties.getMaxWidth());
        assertThat(outputImage.getHeight()).isLessThan(1000);
    }

    @Test
    void process_ShouldNotUpscaleSmallImage() throws IOException {
        InputStream inputStream = createImage(500, 500, "jpg");
        imageProperties.setMaxWidth(1080);
        imageProperties.setMaxHeight(1080);

        ProcessedImage result = processor.process(inputStream);

        BufferedImage outputImage = ImageIO.read(result.content());
        assertThat(outputImage.getWidth()).isEqualTo(500);
        assertThat(outputImage.getHeight()).isEqualTo(500);
    }

    @Test
    void process_WhenDataIsInvalid_ShouldThrowException() {
        InputStream inputStream = new ByteArrayInputStream(new byte[]{0, 1, 2, 3});

        String message = assertThrows(InternalException.class, () -> processor.process(inputStream)).getMessage();
        assertTrue(message.contains("Image processing error"));
    }

    private InputStream createImage(int width, int height, String format) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.RED);
        graphics.fillRect(0, 0, width, height);
        graphics.drawString("content", 10, 10);
        graphics.dispose();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, format, outputStream);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }
}