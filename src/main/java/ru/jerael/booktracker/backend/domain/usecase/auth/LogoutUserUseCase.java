package ru.jerael.booktracker.backend.domain.usecase.auth;

import ru.jerael.booktracker.backend.domain.model.auth.LogoutPayload;

public interface LogoutUserUseCase {
    void execute(LogoutPayload data);
}
