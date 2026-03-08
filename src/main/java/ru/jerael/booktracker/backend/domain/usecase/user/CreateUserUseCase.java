package ru.jerael.booktracker.backend.domain.usecase.user;

import ru.jerael.booktracker.backend.domain.model.user.UserCreation;
import ru.jerael.booktracker.backend.domain.model.user.UserCreationResult;

public interface CreateUserUseCase {
    UserCreationResult execute(UserCreation data);
}
