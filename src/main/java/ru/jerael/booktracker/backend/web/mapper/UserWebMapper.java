package ru.jerael.booktracker.backend.web.mapper;

import org.springframework.stereotype.Component;
import ru.jerael.booktracker.backend.domain.model.user.User;
import ru.jerael.booktracker.backend.domain.model.user.UserCreation;
import ru.jerael.booktracker.backend.domain.model.user.UserCreationResult;
import ru.jerael.booktracker.backend.web.dto.user.UserCreationRequest;
import ru.jerael.booktracker.backend.web.dto.user.UserCreationResponse;
import ru.jerael.booktracker.backend.web.dto.user.UserResponse;

@Component
public class UserWebMapper {
    public UserCreationResponse toResponse(UserCreationResult result) {
        if (result == null) return null;

        return new UserCreationResponse(
            result.userId(),
            result.email(),
            result.expiresAt()
        );
    }

    public UserCreation toDomain(UserCreationRequest request) {
        if (request == null) return null;

        return new UserCreation(
            request.email(),
            request.password()
        );
    }

    public UserResponse toResponse(User user) {
        if (user == null) return null;

        return new UserResponse(
            user.id(),
            user.email(),
            user.isVerified(),
            user.createdAt()
        );
    }
}
