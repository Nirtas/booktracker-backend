package ru.jerael.booktracker.backend.domain.exception.factory;

import ru.jerael.booktracker.backend.domain.exception.AlreadyExistsException;
import ru.jerael.booktracker.backend.domain.exception.NotFoundException;
import ru.jerael.booktracker.backend.domain.exception.UnauthenticatedException;
import ru.jerael.booktracker.backend.domain.exception.ValidationException;
import ru.jerael.booktracker.backend.domain.exception.code.UserErrorCode;
import ru.jerael.booktracker.backend.domain.exception.model.ValidationError;
import java.util.Map;
import java.util.UUID;

public class UserExceptionFactory {
    public static AlreadyExistsException emailAlreadyExists(String email) {
        return new AlreadyExistsException(
            UserErrorCode.EMAIL_ALREADY_EXISTS,
            "User with email " + email + " already exists"
        );
    }

    public static NotFoundException userNotFound(UUID id) {
        return new NotFoundException(
            UserErrorCode.USER_NOT_FOUND,
            "User with id " + id + " was not found"
        );
    }

    public static ValidationException userAlreadyVerified(UUID id) {
        return new ValidationException(
            new ValidationError(
                UserErrorCode.ALREADY_VERIFIED.name(),
                "userId",
                "User with id " + id + " already verified",
                Map.of()
            )
        );
    }

    public static UnauthenticatedException invalidCredentials() {
        return new UnauthenticatedException(
            UserErrorCode.INVALID_CREDENTIALS,
            "Invalid email or password"
        );
    }

    public static ValidationException userNotVerified(String email) {
        return new ValidationException(
            new ValidationError(
                UserErrorCode.USER_NOT_VERIFIED.name(),
                "email",
                "Email " + email + " is not verified",
                Map.of()
            )
        );
    }
}
