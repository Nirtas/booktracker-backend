package ru.jerael.booktracker.backend.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.time.Duration;

@Component
@Data
@ConfigurationProperties(prefix = "app.rate-limit")
public class RateLimitProperties {
    private int capacity;
    private int refillAmount;
    private Duration refillDuration;
}
