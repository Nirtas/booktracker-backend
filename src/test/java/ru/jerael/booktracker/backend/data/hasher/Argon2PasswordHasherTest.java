package ru.jerael.booktracker.backend.data.hasher;

import org.junit.jupiter.api.Test;
import ru.jerael.booktracker.backend.data.hasher.config.Argon2Properties;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class Argon2PasswordHasherTest {
    private final Argon2PasswordHasher hasher;

    public Argon2PasswordHasherTest() {
        Argon2Properties properties = new Argon2Properties();
        properties.setSaltLength(4);
        properties.setHashLength(8);
        properties.setParallelism(1);
        properties.setMemory(16);
        properties.setIterations(1);
        hasher = new Argon2PasswordHasher(properties);
    }

    @Test
    void hash_ShouldHashPassword() {
        String password = "password";

        String hash = hasher.hash(password);

        assertNotNull(hash);
        assertThat(hash).startsWith("$argon2id$");
        assertNotEquals(password, hash);
    }

    @Test
    void verify_ShouldVerifyCorrectPassword() {
        String password = "password";
        String hash = hasher.hash(password);

        boolean isValid = hasher.verify(password, hash);

        assertTrue(isValid);
    }

    @Test
    void verify_ShouldNotVerifyIncorrectPassword() {
        String correctPassword = "correct password";
        String wrongPassword = "wrong password";
        String hash = hasher.hash(correctPassword);

        boolean isValid = hasher.verify(wrongPassword, hash);

        assertFalse(isValid);
    }

    @Test
    void hash_ShouldProduceDifferentHashesForSamePassword() {
        String password = "password";

        String hash1 = hasher.hash(password);
        String hash2 = hasher.hash(password);

        assertNotEquals(hash1, hash2);
        assertTrue(hasher.verify(password, hash1));
        assertTrue(hasher.verify(password, hash2));
    }
}