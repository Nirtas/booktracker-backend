package ru.jerael.booktracker.backend.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.jerael.booktracker.backend.domain.hasher.PasswordHasher;
import ru.jerael.booktracker.backend.domain.model.auth.GeneratedToken;
import ru.jerael.booktracker.backend.domain.model.auth.IdentityTokenType;
import ru.jerael.booktracker.backend.domain.model.auth.RefreshToken;
import ru.jerael.booktracker.backend.domain.model.auth.TokenPair;
import ru.jerael.booktracker.backend.domain.repository.RefreshTokenRepository;
import ru.jerael.booktracker.backend.domain.service.token.IdentityTokenProvider;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthTokenServiceImplTest {

    @Mock
    private IdentityTokenProvider identityTokenProvider;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private AuthTokenServiceImpl service;

    @Captor
    private ArgumentCaptor<Map<String, Object>> claimsCaptor;

    private final UUID userId = UUID.fromString("2c5781ea-1bc2-4561-a83d-26106df2526e");
    private final Instant expiresAt = Instant.ofEpochMilli(1771249999347L);

    @Test
    void issueTokens_ShouldSaveHashedRefreshTokenAndReturnTokenPair() {
        String accessToken = "access token";
        String refreshToken = "refresh token";
        String hash = "refresh token hash";
        when(identityTokenProvider.generateToken(any(), eq(IdentityTokenType.ACCESS)))
            .thenReturn(new GeneratedToken(accessToken, expiresAt));
        when(identityTokenProvider.generateToken(any(), eq(IdentityTokenType.REFRESH)))
            .thenReturn(new GeneratedToken(refreshToken, expiresAt));
        when(passwordHasher.hash(refreshToken)).thenReturn(hash);

        TokenPair result = service.issueTokens(userId);

        assertEquals(accessToken, result.accessToken());
        assertEquals(refreshToken, result.refreshToken());

        verify(identityTokenProvider).generateToken(claimsCaptor.capture(), eq(IdentityTokenType.ACCESS));
        verify(identityTokenProvider).generateToken(claimsCaptor.capture(), eq(IdentityTokenType.REFRESH));

        List<Map<String, Object>> capturedClaims = claimsCaptor.getAllValues();
        assertEquals(userId, capturedClaims.get(0).get("sub"));
        assertEquals(userId, capturedClaims.get(1).get("sub"));

        verify(passwordHasher).hash(refreshToken);

        ArgumentCaptor<RefreshToken> refreshTokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(refreshTokenCaptor.capture());

        RefreshToken savedToken = refreshTokenCaptor.getValue();
        assertEquals(userId, savedToken.userId());
        assertEquals(hash, savedToken.tokenHash());
        assertEquals(expiresAt, savedToken.expiresAt());
    }
}