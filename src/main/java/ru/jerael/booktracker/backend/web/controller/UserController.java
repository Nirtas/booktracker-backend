package ru.jerael.booktracker.backend.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.jerael.booktracker.backend.domain.model.user.User;
import ru.jerael.booktracker.backend.domain.usecase.user.GetUserUseCase;
import ru.jerael.booktracker.backend.web.dto.user.UserResponse;
import ru.jerael.booktracker.backend.web.mapper.UserWebMapper;
import java.util.UUID;

@Tag(name = "Users")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final GetUserUseCase getUserUseCase;
    private final UserWebMapper userWebMapper;

    @Operation(summary = "Get current user details")
    @GetMapping("/me")
    public UserResponse getMe(@AuthenticationPrincipal UUID userId) {
        User user = getUserUseCase.execute(userId);
        return userWebMapper.toResponse(user);
    }
}
