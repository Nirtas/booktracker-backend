package ru.jerael.booktracker.backend.web.mapper;

import org.springframework.stereotype.Component;
import ru.jerael.booktracker.backend.domain.model.auth.ConfirmRegistration;
import ru.jerael.booktracker.backend.domain.model.auth.TokenPair;
import ru.jerael.booktracker.backend.domain.model.auth.UserLogin;
import ru.jerael.booktracker.backend.web.dto.auth.AuthResponse;
import ru.jerael.booktracker.backend.web.dto.auth.ConfirmRegistrationRequest;
import ru.jerael.booktracker.backend.web.dto.auth.LoginRequest;

@Component
public class AuthWebMapper {
    public ConfirmRegistration toDomain(ConfirmRegistrationRequest request) {
        if (request == null) return null;

        return new ConfirmRegistration(
            request.userId(),
            request.token()
        );
    }

    public AuthResponse toResponse(TokenPair tokenPair) {
        if (tokenPair == null) return null;

        return new AuthResponse(
            tokenPair.accessToken(),
            tokenPair.refreshToken()
        );
    }

    public UserLogin toDomain(LoginRequest request) {
        if (request == null) return null;

        return new UserLogin(
            request.email(),
            request.password()
        );
    }
}
