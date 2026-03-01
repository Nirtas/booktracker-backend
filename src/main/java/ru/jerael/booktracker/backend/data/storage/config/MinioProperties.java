package ru.jerael.booktracker.backend.data.storage.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.time.Duration;

@Component
@Data
@ConfigurationProperties(prefix = "app.minio")
public class MinioProperties {
    private String url;
    private String user;
    private String password;
    private Duration urlExpiry = Duration.ofHours(1);
    private Buckets buckets;

    @Data
    public static class Buckets {
        private String covers;
    }
}
