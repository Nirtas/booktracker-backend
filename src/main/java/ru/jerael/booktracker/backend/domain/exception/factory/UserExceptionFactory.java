package ru.jerael.booktracker.backend.domain.exception.factory;

import ru.jerael.booktracker.backend.domain.exception.AlreadyExistsException;
import ru.jerael.booktracker.backend.domain.exception.code.UserErrorCode;

public class UserExceptionFactory {
    public static AlreadyExistsException emailAlreadyExists(String email) {
        return new AlreadyExistsException(
            UserErrorCode.EMAIL_ALREADY_EXISTS,
            "User with email " + email + " already exists"
        );
    }
}
