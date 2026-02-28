package ru.jerael.booktracker.backend.data.storage.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    private String url;
    private String user;
    private String password;
    private Buckets buckets;

    @Data
    public static class Buckets {
        private String covers;
    }
}
