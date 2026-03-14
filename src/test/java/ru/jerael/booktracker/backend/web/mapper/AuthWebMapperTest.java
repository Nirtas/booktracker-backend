package ru.jerael.booktracker.backend.web.mapper;

import org.junit.jupiter.api.Test;
import ru.jerael.booktracker.backend.domain.model.auth.*;
import ru.jerael.booktracker.backend.domain.model.verification.VerificationType;
import ru.jerael.booktracker.backend.web.dto.auth.*;
import java.time.Instant;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthWebMapperTest {
    private final AuthWebMapper authWebMapper = new AuthWebMapper();

    private final UUID userId = UUID.fromString("ee39af7a-a073-4473-878a-1aae34e98bb7");
    private final String token = "123456";
    private final String accessToken = "access token";
    private final String refreshToken = "refresh token";
    private final String email = "test@example.com";
    private final String password = "password";

    @Test
    void toDomain_ConfirmRegistration() {
        ConfirmRegistrationRequest request = new ConfirmRegistrationRequest(userId, token);

        ConfirmRegistration domain = authWebMapper.toDomain(request);

        assertEquals(userId, domain.userId());
        assertEquals(token, domain.token());
    }

    @Test
    void toResponse() {
        TokenPair tokenPair = new TokenPair(accessToken, refreshToken);

        AuthResponse response = authWebMapper.toResponse(tokenPair);

        assertEquals(accessToken, response.accessToken());
        assertEquals(refreshToken, response.refreshToken());
    }

    @Test
    void toDomain_UserLogin() {
        LoginRequest request = new LoginRequest(email, password);

        UserLogin domain = authWebMapper.toDomain(request);

        assertEquals(email, domain.email());
        assertEquals(password, domain.password());
    }

    @Test
    void toDomain_RefreshTokenPayload() {
        RefreshTokensRequest request = new RefreshTokensRequest(refreshToken);

        RefreshTokenPayload domain = authWebMapper.toDomain(request);

        assertEquals(refreshToken, domain.refreshToken());
    }

    @Test
    void toDomain_LogoutPayload() {
        LogoutRequest request = new LogoutRequest(refreshToken);

        LogoutPayload domain = authWebMapper.toDomain(request);

        assertEquals(refreshToken, domain.refreshToken());
    }

    @Test
    void toDomain_ResendVerification() {
        ResendVerificationRequest request = new ResendVerificationRequest(userId, "REGISTRATION");

        ResendVerification domain = authWebMapper.toDomain(request);

        assertEquals(userId, domain.userId());
        assertEquals(VerificationType.REGISTRATION, domain.type());
    }

    @Test
    void toResponse_ResendVerificationResponse() {
        Instant expiresAt = Instant.now().plusSeconds(600);
        ResendVerificationResult result = new ResendVerificationResult(userId, email, expiresAt);

        ResendVerificationResponse domain = authWebMapper.toResponse(result);

        assertEquals(userId, domain.userId());
        assertEquals(email, domain.email());
        assertEquals(expiresAt, domain.expiresAt());
    }
}