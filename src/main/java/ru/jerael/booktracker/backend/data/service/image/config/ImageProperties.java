package ru.jerael.booktracker.backend.data.service.image.config;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import ru.jerael.booktracker.backend.domain.model.image.ImageFormat;

@Component
@Data
@ConfigurationProperties(prefix = "app.image")
@Validated
public class ImageProperties {
    private ImageFormat targetFormat = ImageFormat.WEBP;

    @DecimalMin("0.1")
    @DecimalMax("1.0")
    private double quality = 0.8;

    @Positive
    private int maxWidth = 1080;

    @Positive
    private int maxHeight = 1080;
}
