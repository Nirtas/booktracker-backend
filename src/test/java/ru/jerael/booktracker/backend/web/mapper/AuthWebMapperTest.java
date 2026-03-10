package ru.jerael.booktracker.backend.web.mapper;

import org.junit.jupiter.api.Test;
import ru.jerael.booktracker.backend.domain.model.auth.ConfirmRegistration;
import ru.jerael.booktracker.backend.domain.model.auth.TokenPair;
import ru.jerael.booktracker.backend.web.dto.auth.AuthResponse;
import ru.jerael.booktracker.backend.web.dto.auth.ConfirmRegistrationRequest;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthWebMapperTest {
    private final AuthWebMapper authWebMapper = new AuthWebMapper();

    private final UUID userId = UUID.fromString("ee39af7a-a073-4473-878a-1aae34e98bb7");
    private final String token = "123456";
    private final String accessToken = "access token";
    private final String refreshToken = "refresh token";

    @Test
    void toDomain() {
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
}