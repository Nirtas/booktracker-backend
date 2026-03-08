package ru.jerael.booktracker.backend.web.mapper;

import org.springframework.stereotype.Component;
import ru.jerael.booktracker.backend.domain.model.user.UserCreation;
import ru.jerael.booktracker.backend.domain.model.user.UserCreationResult;
import ru.jerael.booktracker.backend.web.dto.user.UserCreationRequest;
import ru.jerael.booktracker.backend.web.dto.user.UserCreationResponse;

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
}
