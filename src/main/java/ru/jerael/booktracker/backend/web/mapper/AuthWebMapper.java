package ru.jerael.booktracker.backend.web.mapper;

import org.springframework.stereotype.Component;
import ru.jerael.booktracker.backend.domain.model.auth.*;
import ru.jerael.booktracker.backend.web.dto.auth.*;

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

    public RefreshTokenPayload toDomain(RefreshTokensRequest request) {
        if (request == null) return null;

        return new RefreshTokenPayload(request.refreshToken());
    }

    public LogoutPayload toDomain(LogoutRequest request) {
        if (request == null) return null;

        return new LogoutPayload(request.refreshToken());
    }
}
