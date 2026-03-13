package ru.jerael.booktracker.backend.web.mapper;

import org.junit.jupiter.api.Test;
import ru.jerael.booktracker.backend.domain.model.user.User;
import ru.jerael.booktracker.backend.domain.model.user.UserCreation;
import ru.jerael.booktracker.backend.domain.model.user.UserCreationResult;
import ru.jerael.booktracker.backend.web.dto.user.UserCreationRequest;
import ru.jerael.booktracker.backend.web.dto.user.UserCreationResponse;
import ru.jerael.booktracker.backend.web.dto.user.UserResponse;
import java.time.Instant;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class UserWebMapperTest {
    private final UserWebMapper userWebMapper = new UserWebMapper();

    private final String email = "test@example.com";
    private final String password = "password";
    private final UUID userId = UUID.fromString("ee39af7a-a073-4473-878a-1aae34e98bb7");
    private final Instant expiresAt = Instant.ofEpochMilli(1771249999347L);
    private final Instant createdAt = Instant.ofEpochMilli(1771246999347L);

    @Test
    void toResponse_UserCreationResponse() {
        UserCreationResult result = new UserCreationResult(userId, email, expiresAt);

        UserCreationResponse response = userWebMapper.toResponse(result);

        assertNotNull(response);
        assertEquals(userId, response.userId());
        assertEquals(email, response.email());
        assertEquals(expiresAt, response.expiresAt());
    }

    @Test
    void toDomain() {
        UserCreationRequest request = new UserCreationRequest(email, password);

        UserCreation domain = userWebMapper.toDomain(request);

        assertNotNull(domain);
        assertEquals(email, domain.email());
        assertEquals(password, domain.password());
    }

    @Test
    void toResponse_UserResponse() {
        User user = new User(userId, email, "password hash", true, createdAt);

        UserResponse response = userWebMapper.toResponse(user);

        assertNotNull(response);
        assertEquals(userId, response.userId());
        assertEquals(email, response.email());
        assertTrue(response.isVerified());
        assertEquals(createdAt, response.createdAt());
    }
}