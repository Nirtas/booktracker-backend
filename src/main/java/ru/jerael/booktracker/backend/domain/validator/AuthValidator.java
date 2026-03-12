package ru.jerael.booktracker.backend.domain.validator;

import ru.jerael.booktracker.backend.domain.model.auth.*;

public interface AuthValidator {
    void validateRegistrationConfirmation(ConfirmRegistration data);

    void validateLogin(UserLogin data);

    void validateRefreshTokenPayload(RefreshTokenPayload data);

    void validateLogoutPayload(LogoutPayload data);

    void validateResendVerification(ResendVerification data);
}
