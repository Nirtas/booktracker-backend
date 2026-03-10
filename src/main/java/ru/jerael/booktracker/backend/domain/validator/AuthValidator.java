package ru.jerael.booktracker.backend.domain.validator;

import ru.jerael.booktracker.backend.domain.model.auth.ConfirmRegistration;
import ru.jerael.booktracker.backend.domain.model.auth.UserLogin;

public interface AuthValidator {
    void validateRegistrationConfirmation(ConfirmRegistration data);

    void validateLogin(UserLogin data);
}
