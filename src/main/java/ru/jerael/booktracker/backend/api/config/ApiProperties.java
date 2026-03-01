package ru.jerael.booktracker.backend.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;

@Component
@Data
@ConfigurationProperties(prefix = "app.api")
public class ApiProperties {
    private DataSize maxFileSize = DataSize.ofMegabytes(10);
}
