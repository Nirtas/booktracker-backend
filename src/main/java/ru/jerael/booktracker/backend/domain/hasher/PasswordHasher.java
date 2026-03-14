package ru.jerael.booktracker.backend.domain.hasher;

public interface PasswordHasher {
    String hash(String password);

    boolean verify(String password, String hash);
}
