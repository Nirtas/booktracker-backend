package ru.jerael.booktracker.backend.application.service.token;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jerael.booktracker.backend.application.service.token.config.AuthTokenProperties;
import ru.jerael.booktracker.backend.domain.exception.factory.IdentityTokenExceptionFactory;
import ru.jerael.booktracker.backend.domain.hasher.PasswordHasher;
import ru.jerael.booktracker.backend.domain.model.auth.*;
import ru.jerael.booktracker.backend.domain.repository.RefreshTokenRepository;
import ru.jerael.booktracker.backend.domain.service.token.AuthTokenService;
import ru.jerael.booktracker.backend.domain.service.token.IdentityTokenProvider;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthTokenServiceImpl implements AuthTokenService {
    private final AuthTokenProperties properties;
    private final IdentityTokenProvider identityTokenProvider;
    private final PasswordHasher passwordHasher;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public TokenPair issueTokens(UUID userId) {
        GeneratedToken accessToken = createToken(userId, IdentityTokenType.ACCESS);
        GeneratedToken refreshToken = createToken(userId, IdentityTokenType.REFRESH);
        String hash = passwordHasher.hash(refreshToken.value());
        refreshTokenRepository.save(new RefreshToken(null, userId, hash, refreshToken.expiresAt()));
        return new TokenPair(accessToken.value(), refreshToken.value());
    }

    @Override
    public IdentityTokenClaims verifyToken(String token, IdentityTokenType expectedTokenType) {
        IdentityTokenClaims claims = identityTokenProvider.decode(token);
        if (!properties.getIssuer().equals(claims.issuer())) {
            throw IdentityTokenExceptionFactory.invalidIssuer(claims.issuer());
        }
        if (claims.type() != expectedTokenType) {
            throw IdentityTokenExceptionFactory.invalidTokenType(expectedTokenType.name(), claims.type().name());
        }
        if (Instant.now().isAfter(claims.expiresAt())) {
            throw IdentityTokenExceptionFactory.tokenExpired();
        }
        return claims;
    }

    private GeneratedToken createToken(UUID userId, IdentityTokenType tokenType) {
        Instant issuedAt = Instant.now();
        Duration expiry =
            tokenType == IdentityTokenType.ACCESS ? properties.getAccessExpiry() : properties.getRefreshExpiry();
        Instant expiresAt = issuedAt.plus(expiry);
        IdentityTokenClaims data = new IdentityTokenClaims(
            userId,
            tokenType,
            properties.getIssuer(),
            issuedAt,
            expiresAt
        );
        String value = identityTokenProvider.encode(data);
        return new GeneratedToken(value, expiresAt);
    }
}
