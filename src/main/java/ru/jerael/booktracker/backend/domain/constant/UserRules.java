package ru.jerael.booktracker.backend.domain.constant;

import java.util.Set;

public final class UserRules {
    private UserRules() {}

    public static final int EMAIL_MAX_LENGTH = 255;
    public static final int PASSWORD_HASH_MAX_LENGTH = 255;

    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final Set<Character> PASSWORD_ALLOWED_SPECIAL_CHARS =
        Set.of('!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '+');
}
