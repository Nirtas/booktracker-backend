package ru.jerael.booktracker.backend.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jerael.booktracker.backend.domain.hasher.PasswordHasher;
import ru.jerael.booktracker.backend.domain.model.auth.GeneratedToken;
import ru.jerael.booktracker.backend.domain.model.auth.IdentityTokenType;
import ru.jerael.booktracker.backend.domain.model.auth.RefreshToken;
import ru.jerael.booktracker.backend.domain.model.auth.TokenPair;
import ru.jerael.booktracker.backend.domain.repository.RefreshTokenRepository;
import ru.jerael.booktracker.backend.domain.service.token.AuthTokenService;
import ru.jerael.booktracker.backend.domain.service.token.IdentityTokenProvider;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthTokenServiceImpl implements AuthTokenService {
    private final IdentityTokenProvider identityTokenProvider;
    private final PasswordHasher passwordHasher;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public TokenPair issueTokens(UUID userId) {
        Map<String, Object> claims = Map.of("sub", userId);
        GeneratedToken accessToken = identityTokenProvider.generateToken(claims, IdentityTokenType.ACCESS);
        GeneratedToken refreshToken = identityTokenProvider.generateToken(claims, IdentityTokenType.REFRESH);
        String hash = passwordHasher.hash(refreshToken.value());
        refreshTokenRepository.save(new RefreshToken(null, userId, hash, refreshToken.expiresAt()));
        return new TokenPair(accessToken.value(), refreshToken.value());
    }
}
