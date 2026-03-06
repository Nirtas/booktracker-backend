package ru.jerael.booktracker.backend.data.smtp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "app.smtp")
public class SmtpProperties {
    private String from;
}
