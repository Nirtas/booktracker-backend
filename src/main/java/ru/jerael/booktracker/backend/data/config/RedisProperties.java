package ru.jerael.booktracker.backend.data.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.time.Duration;

@Component
@Data
@ConfigurationProperties(prefix = "app.redis")
public class RedisProperties {
    private Duration ttl = Duration.ofHours(24);
}
