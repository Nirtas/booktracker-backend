package ru.jerael.booktracker.backend.domain.exception.factory;

import ru.jerael.booktracker.backend.domain.exception.code.PasswordErrorCode;
import ru.jerael.booktracker.backend.domain.exception.model.ValidationError;
import java.util.Map;
import java.util.Set;

public class PasswordValidationErrorFactory {
    public static ValidationError needsLowercase() {
        return new ValidationError(
            PasswordErrorCode.NEEDS_LOWERCASE.name(),
            "password",
            "Needs lowercase",
            Map.of()
        );
    }

    public static ValidationError needsUppercase() {
        return new ValidationError(
            PasswordErrorCode.NEEDS_UPPERCASE.name(),
            "password",
            "Needs uppercase",
            Map.of()
        );
    }

    public static ValidationError needsDigit() {
        return new ValidationError(
            PasswordErrorCode.NEEDS_DIGIT.name(),
            "password",
            "Needs digit",
            Map.of()
        );
    }

    public static ValidationError needsSpecialChar(Set<Character> allowedChars) {
        return new ValidationError(
            PasswordErrorCode.NEEDS_SPECIAL_CHAR.name(),
            "password",
            "Needs special character",
            Map.of("allowed", allowedChars)
        );
    }

    public static ValidationError forbiddenChar(Set<Character> forbiddenChars, Set<Character> allowedChars) {
        return new ValidationError(
            PasswordErrorCode.FORBIDDEN_CHAR.name(),
            "password",
            "Contains forbidden characters",
            Map.of(
                "forbidden", forbiddenChars,
                "allowed", allowedChars
            )
        );
    }
}
