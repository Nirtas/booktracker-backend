package ru.jerael.booktracker.backend.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.jerael.booktracker.backend.domain.model.auth.ConfirmRegistration;
import ru.jerael.booktracker.backend.domain.model.auth.TokenPair;
import ru.jerael.booktracker.backend.domain.model.auth.UserLogin;
import ru.jerael.booktracker.backend.domain.model.user.UserCreation;
import ru.jerael.booktracker.backend.domain.model.user.UserCreationResult;
import ru.jerael.booktracker.backend.domain.usecase.auth.ConfirmRegistrationUseCase;
import ru.jerael.booktracker.backend.domain.usecase.auth.LoginUserUseCase;
import ru.jerael.booktracker.backend.domain.usecase.user.CreateUserUseCase;
import ru.jerael.booktracker.backend.web.dto.auth.AuthResponse;
import ru.jerael.booktracker.backend.web.dto.auth.ConfirmRegistrationRequest;
import ru.jerael.booktracker.backend.web.dto.auth.LoginRequest;
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
}
