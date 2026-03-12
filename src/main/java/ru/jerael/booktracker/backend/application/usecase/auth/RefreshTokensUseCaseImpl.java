package ru.jerael.booktracker.backend.application.usecase.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.jerael.booktracker.backend.domain.exception.UnauthenticatedException;
import ru.jerael.booktracker.backend.domain.exception.factory.IdentityTokenExceptionFactory;
import ru.jerael.booktracker.backend.domain.hasher.PasswordHasher;
import ru.jerael.booktracker.backend.domain.model.auth.*;
import ru.jerael.booktracker.backend.domain.repository.RefreshTokenRepository;
import ru.jerael.booktracker.backend.domain.service.token.AuthTokenService;
import ru.jerael.booktracker.backend.domain.usecase.auth.RefreshTokensUseCase;
import ru.jerael.booktracker.backend.domain.validator.AuthValidator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RefreshTokensUseCaseImpl implements RefreshTokensUseCase {
    private final AuthValidator authValidator;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordHasher passwordHasher;
    private final AuthTokenService authTokenService;

    @Override
    public TokenPair execute(RefreshTokenPayload data) {
        authValidator.validateRefreshTokenPayload(data);
        String refreshToken = data.refreshToken();

        IdentityTokenClaims claims;
        try {
            claims = authTokenService.verifyToken(refreshToken, IdentityTokenType.REFRESH);
        } catch (UnauthenticatedException e) {
            throw IdentityTokenExceptionFactory.invalidToken();
        }

        List<RefreshToken> refreshTokens = refreshTokenRepository.findAllByUserId(claims.userId());
        RefreshToken foundRefreshToken = refreshTokens.stream()
            .filter(token -> passwordHasher.verify(refreshToken, token.tokenHash()))
            .findFirst()
            .orElseThrow(IdentityTokenExceptionFactory::invalidToken);
        refreshTokenRepository.deleteById(foundRefreshToken.id());
        return authTokenService.issueTokens(claims.userId());
    }
}
