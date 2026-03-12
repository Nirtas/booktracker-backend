package ru.jerael.booktracker.backend.application.service.token;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.jerael.booktracker.backend.application.service.token.config.AuthTokenProperties;
import ru.jerael.booktracker.backend.domain.hasher.PasswordHasher;
import ru.jerael.booktracker.backend.domain.model.auth.IdentityTokenClaims;
import ru.jerael.booktracker.backend.domain.model.auth.IdentityTokenType;
import ru.jerael.booktracker.backend.domain.model.auth.RefreshToken;
import ru.jerael.booktracker.backend.domain.model.auth.TokenPair;
import ru.jerael.booktracker.backend.domain.repository.RefreshTokenRepository;
import ru.jerael.booktracker.backend.domain.service.token.IdentityTokenProvider;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthTokenServiceImplTest {

    @Mock
    private AuthTokenProperties properties;

    @Mock
    private IdentityTokenProvider identityTokenProvider;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private AuthTokenServiceImpl service;

    @Captor
    private ArgumentCaptor<IdentityTokenClaims> claimsCaptor;

    private final UUID userId = UUID.fromString("2c5781ea-1bc2-4561-a83d-26106df2526e");

    @Test
    void issueTokens_ShouldSaveHashedRefreshTokenAndReturnTokenPair() {
        String accessToken = "access token";
        String refreshToken = "refresh token";
        String hash = "refresh token hash";
        when(properties.getAccessExpiry()).thenReturn(Duration.ofMinutes(15L));
        when(properties.getRefreshExpiry()).thenReturn(Duration.ofDays(30L));
        when(properties.getIssuer()).thenReturn("issuer");

        when(identityTokenProvider.encode(any(IdentityTokenClaims.class)))
            .thenAnswer(invocationOnMock -> {
                IdentityTokenClaims claims = invocationOnMock.getArgument(0);
                if (claims.type() == IdentityTokenType.ACCESS) {
                    return accessToken;
                } else if (claims.type() == IdentityTokenType.REFRESH) {
                    return refreshToken;
                }
                return null;
            });

        when(passwordHasher.hash(refreshToken)).thenReturn(hash);

        TokenPair result = service.issueTokens(userId);

        assertEquals(accessToken, result.accessToken());
        assertEquals(refreshToken, result.refreshToken());

        verify(identityTokenProvider, times(2)).encode(claimsCaptor.capture());

        List<IdentityTokenClaims> capturedClaims = claimsCaptor.getAllValues();

        IdentityTokenClaims accessClaims = capturedClaims.get(0);
        assertEquals(userId, accessClaims.userId());
        assertEquals(IdentityTokenType.ACCESS, accessClaims.type());
        assertThat(accessClaims.expiresAt()).isAfter(Instant.now());

        IdentityTokenClaims refreshClaims = capturedClaims.get(1);
        assertEquals(userId, refreshClaims.userId());
        assertEquals(IdentityTokenType.REFRESH, refreshClaims.type());
        assertThat(refreshClaims.expiresAt()).isAfter(accessClaims.expiresAt());

        verify(passwordHasher).hash(refreshToken);

        ArgumentCaptor<RefreshToken> refreshTokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(refreshTokenCaptor.capture());

        RefreshToken savedToken = refreshTokenCaptor.getValue();
        assertEquals(userId, savedToken.userId());
        assertEquals(hash, savedToken.tokenHash());
        assertEquals(refreshClaims.expiresAt(), savedToken.expiresAt());
    }
}