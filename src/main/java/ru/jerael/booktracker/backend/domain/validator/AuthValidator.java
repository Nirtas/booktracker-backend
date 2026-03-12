package ru.jerael.booktracker.backend.domain.validator;

import ru.jerael.booktracker.backend.domain.model.auth.ConfirmRegistration;
import ru.jerael.booktracker.backend.domain.model.auth.LogoutPayload;
import ru.jerael.booktracker.backend.domain.model.auth.RefreshTokenPayload;
import ru.jerael.booktracker.backend.domain.model.auth.UserLogin;

public interface AuthValidator {
    void validateRegistrationConfirmation(ConfirmRegistration data);

    void validateLogin(UserLogin data);

    void validateRefreshTokenPayload(RefreshTokenPayload data);

    void validateLogoutPayload(LogoutPayload data);
}
