package ru.jerael.booktracker.backend.data.external.google.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "app.external.google-books")
public class GoogleBooksProperties {
    private String baseUrl;
    private String apiKey;
}
