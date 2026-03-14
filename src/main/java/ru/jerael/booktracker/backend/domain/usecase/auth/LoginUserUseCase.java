package ru.jerael.booktracker.backend.domain.usecase.auth;

import ru.jerael.booktracker.backend.domain.model.auth.TokenPair;
import ru.jerael.booktracker.backend.domain.model.auth.UserLogin;

public interface LoginUserUseCase {
    TokenPair execute(UserLogin data);
}
