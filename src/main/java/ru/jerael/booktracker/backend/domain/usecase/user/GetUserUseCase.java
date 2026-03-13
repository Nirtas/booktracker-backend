package ru.jerael.booktracker.backend.domain.usecase.user;

import ru.jerael.booktracker.backend.domain.model.user.User;
import java.util.UUID;

public interface GetUserUseCase {
    User execute(UUID userId);
}
