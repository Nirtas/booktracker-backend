package ru.jerael.booktracker.backend.domain.exception.factory;

import ru.jerael.booktracker.backend.domain.exception.AlreadyExistsException;
import ru.jerael.booktracker.backend.domain.exception.NotFoundException;
import ru.jerael.booktracker.backend.domain.exception.ValidationException;
import ru.jerael.booktracker.backend.domain.exception.code.UserErrorCode;
import ru.jerael.booktracker.backend.domain.exception.model.ValidationError;
import java.util.List;
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
        return new ValidationException(List.of(
            new ValidationError(
                UserErrorCode.ALREADY_VERIFIED.name(),
                "userId",
                "User with id " + id + " already verified",
                Map.of()
            )
        ));
    }
}
