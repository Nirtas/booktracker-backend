package ru.jerael.booktracker.backend.data.hasher.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "app.argon2")
public class Argon2Properties {
    private int saltLength = 16;
    private int hashLength = 32;
    private int parallelism = 1;
    private int memory = 16384;
    private int iterations = 2;
}
