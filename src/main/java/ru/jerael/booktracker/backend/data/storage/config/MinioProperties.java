package ru.jerael.booktracker.backend.data.storage.config;

import lombok.Data;

@Data
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
