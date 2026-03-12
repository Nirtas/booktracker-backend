package ru.jerael.booktracker.backend.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.jerael.booktracker.backend.domain.model.auth.*;
import ru.jerael.booktracker.backend.domain.model.user.UserCreation;
import ru.jerael.booktracker.backend.domain.model.user.UserCreationResult;
import ru.jerael.booktracker.backend.domain.usecase.auth.*;
import ru.jerael.booktracker.backend.domain.usecase.user.CreateUserUseCase;
import ru.jerael.booktracker.backend.web.dto.auth.*;
import ru.jerael.booktracker.backend.web.dto.user.UserCreationRequest;
import ru.jerael.booktracker.backend.web.dto.user.UserCreationResponse;
import ru.jerael.booktracker.backend.web.mapper.AuthWebMapper;
import ru.jerael.booktracker.backend.web.mapper.UserWebMapper;

@Tag(name = "Auth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserWebMapper userWebMapper;
    private final AuthWebMapper authWebMapper;
    private final CreateUserUseCase createUserUseCase;
    private final ConfirmRegistrationUseCase confirmRegistrationUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final RefreshTokensUseCase refreshTokensUseCase;
    private final LogoutUserUseCase logoutUserUseCase;
    private final ResendVerificationUseCase resendVerificationUseCase;

    @Operation(summary = "Register new user")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserCreationResponse register(@Valid @RequestBody UserCreationRequest request) {
        UserCreation data = userWebMapper.toDomain(request);
        UserCreationResult result = createUserUseCase.execute(data);
        return userWebMapper.toResponse(result);
    }

    @Operation(summary = "Confirm registration")
    @PostMapping("/confirm-registration")
    public AuthResponse confirmRegistration(@Valid @RequestBody ConfirmRegistrationRequest request) {
        ConfirmRegistration data = authWebMapper.toDomain(request);
        TokenPair tokenPair = confirmRegistrationUseCase.execute(data);
        return authWebMapper.toResponse(tokenPair);
    }

    @Operation(summary = "Login")
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        UserLogin data = authWebMapper.toDomain(request);
        TokenPair tokenPair = loginUserUseCase.execute(data);
        return authWebMapper.toResponse(tokenPair);
    }

    @Operation(summary = "Refresh tokens")
    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshTokensRequest request) {
        RefreshTokenPayload data = authWebMapper.toDomain(request);
        TokenPair tokenPair = refreshTokensUseCase.execute(data);
        return authWebMapper.toResponse(tokenPair);
    }

    @Operation(summary = "Logout")
    @PostMapping("/logout")
    public void logout(@Valid @RequestBody LogoutRequest request) {
        LogoutPayload data = authWebMapper.toDomain(request);
        logoutUserUseCase.execute(data);
    }

    @Operation(summary = "Resend verification code")
    @PostMapping("/resend-code")
    public ResendVerificationResponse resendCode(@Valid @RequestBody ResendVerificationRequest request) {
        ResendVerification data = authWebMapper.toDomain(request);
        ResendVerificationResult result = resendVerificationUseCase.execute(data);
        return authWebMapper.toResponse(result);
    }
}
