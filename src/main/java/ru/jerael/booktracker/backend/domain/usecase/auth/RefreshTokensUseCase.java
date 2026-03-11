package ru.jerael.booktracker.backend.domain.usecase.auth;

import ru.jerael.booktracker.backend.domain.model.auth.RefreshTokenPayload;
import ru.jerael.booktracker.backend.domain.model.auth.TokenPair;

public interface RefreshTokensUseCase {
    TokenPair execute(RefreshTokenPayload data);
}
