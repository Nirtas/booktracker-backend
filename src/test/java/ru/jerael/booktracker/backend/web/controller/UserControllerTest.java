package ru.jerael.booktracker.backend.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import ru.jerael.booktracker.backend.domain.model.user.User;
import ru.jerael.booktracker.backend.domain.service.token.AuthTokenService;
import ru.jerael.booktracker.backend.domain.usecase.user.GetUserUseCase;
import ru.jerael.booktracker.backend.web.config.WebProperties;
import ru.jerael.booktracker.backend.web.dto.user.UserResponse;
import ru.jerael.booktracker.backend.web.exception.handler.GlobalExceptionHandler;
import ru.jerael.booktracker.backend.web.mapper.UserWebMapper;
import ru.jerael.booktracker.backend.web.security.SecurityConfig;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

@WebMvcTest(UserController.class)
@Import({GlobalExceptionHandler.class, UserWebMapper.class, SecurityConfig.class})
class UserControllerTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private WebProperties webProperties;

    @MockitoBean
    private AuthTokenService authTokenService;

    @MockitoBean
    private GetUserUseCase getUserUseCase;

    @MockitoBean
    private UserWebMapper userWebMapper;

    private final UUID userId = UUID.fromString("ee39af7a-a073-4473-878a-1aae34e98bb7");
    private final String email = "test@example.com";
    private final String passwordHash = "password hash";
    private final Instant createdAt = Instant.ofEpochMilli(1771249699347L);

    private UsernamePasswordAuthenticationToken getAuth() {
        return new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
    }

    @Test
    void getMe_WhenAuthenticated_ShouldReturnUserResponse() {
        User user = new User(userId, email, passwordHash, true, createdAt);
        UserResponse userResponse = new UserResponse(userId, email, true, createdAt);
        when(getUserUseCase.execute(userId)).thenReturn(user);
        when(userWebMapper.toResponse(user)).thenReturn(userResponse);

        var mockResponse = mockMvcTester.get().uri("/api/v1/users/me").with(authentication(getAuth()));

        assertThat(mockResponse)
            .hasStatus(HttpStatus.OK)
            .bodyJson()
            .extractingPath("$.email")
            .isEqualTo(email);
    }
}