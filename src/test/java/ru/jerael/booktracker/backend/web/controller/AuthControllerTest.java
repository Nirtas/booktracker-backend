package ru.jerael.booktracker.backend.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import ru.jerael.booktracker.backend.domain.exception.factory.UserExceptionFactory;
import ru.jerael.booktracker.backend.domain.model.auth.ConfirmRegistration;
import ru.jerael.booktracker.backend.domain.model.auth.TokenPair;
import ru.jerael.booktracker.backend.domain.model.user.UserCreation;
import ru.jerael.booktracker.backend.domain.model.user.UserCreationResult;
import ru.jerael.booktracker.backend.domain.usecase.auth.ConfirmRegistrationUseCase;
import ru.jerael.booktracker.backend.domain.usecase.user.CreateUserUseCase;
import ru.jerael.booktracker.backend.web.config.WebProperties;
import ru.jerael.booktracker.backend.web.dto.auth.AuthResponse;
import ru.jerael.booktracker.backend.web.dto.auth.ConfirmRegistrationRequest;
import ru.jerael.booktracker.backend.web.dto.user.UserCreationRequest;
import ru.jerael.booktracker.backend.web.dto.user.UserCreationResponse;
import ru.jerael.booktracker.backend.web.mapper.AuthWebMapper;
import ru.jerael.booktracker.backend.web.mapper.UserWebMapper;
import tools.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(AuthController.class)
@Import({UserWebMapper.class, AuthWebMapper.class})
class AuthControllerTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private WebProperties webProperties;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateUserUseCase createUserUseCase;

    @MockitoBean
    private ConfirmRegistrationUseCase confirmRegistrationUseCase;

    private final String email = "test@example.com";
    private final String password = "Password123!";
    private final UUID userId = UUID.fromString("ee39af7a-a073-4473-878a-1aae34e98bb7");
    private final Instant expiresAt = Instant.ofEpochMilli(1771249999347L);
    private final String token = "123456";
    private final String accessToken = "access token";
    private final String refreshToken = "refresh token";

    @Test
    void register_ShouldReturnCreatedUser() {
        UserCreationRequest request = new UserCreationRequest(email, password);
        UserCreationResult mockResult = new UserCreationResult(userId, email, expiresAt);
        when(createUserUseCase.execute(any(UserCreation.class))).thenReturn(mockResult);

        assertThat(
            mockMvcTester.post().uri("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .hasStatus(HttpStatus.CREATED)
            .bodyJson()
            .convertTo(UserCreationResponse.class)
            .satisfies(response -> {
                assertThat(response.userId()).isEqualTo(userId);
                assertThat(response.email()).isEqualTo(email);
                assertThat(response.expiresAt()).isEqualTo(expiresAt);
            });
    }

    @Test
    void register_WhenEmailAlreadyExists_ShouldReturnConflict() {
        String existingEmail = "existing@example.com";
        UserCreationRequest request = new UserCreationRequest(existingEmail, password);
        when(createUserUseCase.execute(any(UserCreation.class)))
            .thenThrow(UserExceptionFactory.emailAlreadyExists(existingEmail));

        assertThat(
            mockMvcTester.post().uri("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .hasStatus(HttpStatus.CONFLICT)
            .bodyJson()
            .extractingPath("$.detail").asString().contains(existingEmail);
    }

    @Test
    void confirmRegistration_ShouldReturnTokens_WhenRequestIsValid() {
        ConfirmRegistrationRequest request = new ConfirmRegistrationRequest(userId, token);
        TokenPair tokenPair = new TokenPair(accessToken, refreshToken);
        when(confirmRegistrationUseCase.execute(any(ConfirmRegistration.class))).thenReturn(tokenPair);

        assertThat(
            mockMvcTester.post().uri("/api/v1/auth/confirm-registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .hasStatus(HttpStatus.OK)
            .bodyJson()
            .convertTo(AuthResponse.class)
            .satisfies(response -> {
                assertEquals(accessToken, response.accessToken());
                assertEquals(refreshToken, response.refreshToken());
            });
    }
}