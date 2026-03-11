package ru.jerael.booktracker.backend.application.usecase.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.jerael.booktracker.backend.domain.exception.factory.UserExceptionFactory;
import ru.jerael.booktracker.backend.domain.hasher.PasswordHasher;
import ru.jerael.booktracker.backend.domain.model.auth.IdentityTokenType;
import ru.jerael.booktracker.backend.domain.model.auth.RefreshToken;
import ru.jerael.booktracker.backend.domain.model.auth.RefreshTokenPayload;
import ru.jerael.booktracker.backend.domain.model.auth.TokenPair;
import ru.jerael.booktracker.backend.domain.repository.RefreshTokenRepository;
import ru.jerael.booktracker.backend.domain.service.token.AuthTokenService;
import ru.jerael.booktracker.backend.domain.service.token.IdentityTokenProvider;
import ru.jerael.booktracker.backend.domain.usecase.auth.RefreshTokensUseCase;
import ru.jerael.booktracker.backend.domain.validator.AuthValidator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokensUseCaseImpl implements RefreshTokensUseCase {
    private final AuthValidator authValidator;
    private final IdentityTokenProvider identityTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordHasher passwordHasher;
    private final AuthTokenService authTokenService;

    @Override
    public TokenPair execute(RefreshTokenPayload data) {
        authValidator.validateRefreshTokenPayload(data);
        String refreshToken = data.refreshToken();
        Map<String, Object> claims = identityTokenProvider.extractClaims(refreshToken, IdentityTokenType.REFRESH);
        String actualType = claims.get("type").toString();
        if (!actualType.equals(IdentityTokenType.REFRESH.name())) {
            throw UserExceptionFactory.invalidCredentials();
        }
        UUID userId = UUID.fromString(claims.get("sub").toString());
        List<RefreshToken> refreshTokens = refreshTokenRepository.findAllByUserId(userId);
        RefreshToken foundRefreshToken = refreshTokens.stream()
            .filter(token -> passwordHasher.verify(refreshToken, token.tokenHash()))
            .findFirst()
            .orElseThrow(UserExceptionFactory::invalidCredentials);
        refreshTokenRepository.deleteById(foundRefreshToken.id());
        return authTokenService.issueTokens(userId);
    }
}
