package ru.jerael.booktracker.backend.data.service.token.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.time.Duration;

@Component
@Data
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {
    private String secret;
    private Duration accessExpiry = Duration.ofMinutes(10);
    private Duration refreshExpiry = Duration.ofDays(30);
    private String issuer;
}
