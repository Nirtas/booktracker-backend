package ru.jerael.booktracker.backend.application.usecase.auth;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ru.jerael.booktracker.backend.application.annotation.UseCase;
import ru.jerael.booktracker.backend.domain.model.auth.RefreshTokenPayload;
import ru.jerael.booktracker.backend.domain.model.auth.TokenPair;
import ru.jerael.booktracker.backend.domain.service.token.AuthTokenService;
import ru.jerael.booktracker.backend.domain.usecase.auth.RefreshTokensUseCase;
import ru.jerael.booktracker.backend.domain.validator.AuthValidator;
import java.util.UUID;

@UseCase
@RequiredArgsConstructor
public class RefreshTokensUseCaseImpl implements RefreshTokensUseCase {
    private final AuthValidator authValidator;
    private final AuthTokenService authTokenService;

    @Override
    @Transactional
    public TokenPair execute(RefreshTokenPayload data) {
        authValidator.validateRefreshTokenPayload(data);
        UUID userId = authTokenService.revokeToken(data.refreshToken());
        return authTokenService.issueTokens(userId);
    }
}
