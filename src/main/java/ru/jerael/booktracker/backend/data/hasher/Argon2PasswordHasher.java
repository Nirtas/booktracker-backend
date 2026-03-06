package ru.jerael.booktracker.backend.data.hasher;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.jerael.booktracker.backend.data.hasher.config.Argon2Properties;
import ru.jerael.booktracker.backend.domain.hasher.PasswordHasher;

@Service
public class Argon2PasswordHasher implements PasswordHasher {
    private final Argon2PasswordEncoder encoder;

    public Argon2PasswordHasher(Argon2Properties properties) {
        this.encoder = new Argon2PasswordEncoder(
            properties.getSaltLength(),
            properties.getHashLength(),
            properties.getParallelism(),
            properties.getMemory(),
            properties.getIterations()
        );
    }

    @Override
    public String hash(String password) {
        return encoder.encode(password);
    }

    @Override
    public boolean verify(String password, String hash) {
        if (hash == null || hash.isBlank()) return false;

        return encoder.matches(password, hash);
    }
}
