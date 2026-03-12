package ru.jerael.booktracker.backend.application.service.token;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.jerael.booktracker.backend.application.service.token.config.AuthTokenProperties;
import ru.jerael.booktracker.backend.domain.exception.UnauthenticatedException;
import ru.jerael.booktracker.backend.domain.exception.code.IdentityTokenErrorCode;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private final String token = "token";

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

    @Test
    void verifyToken_WhenTokenIsValid_ShouldReturnClaims() {
        IdentityTokenClaims claims = new IdentityTokenClaims(
            userId,
            IdentityTokenType.ACCESS,
            "issuer",
            Instant.now().minusSeconds(100),
            Instant.now().plusSeconds(1000)
        );
        when(properties.getIssuer()).thenReturn("issuer");
        when(identityTokenProvider.decode(token)).thenReturn(claims);

        IdentityTokenClaims result = service.verifyToken(token, IdentityTokenType.ACCESS);

        assertEquals(claims, result);
        assertEquals(userId, result.userId());
    }

    @Test
    void verifyToken_WhenIssuerIsInvalid_ShouldThrowException() {
        IdentityTokenClaims claims = new IdentityTokenClaims(
            userId,
            IdentityTokenType.ACCESS,
            "wrong issuer",
            Instant.now().minusSeconds(100),
            Instant.now().plusSeconds(1000)
        );
        when(properties.getIssuer()).thenReturn("issuer");
        when(identityTokenProvider.decode(token)).thenReturn(claims);

        UnauthenticatedException exception =
            assertThrows(UnauthenticatedException.class, () -> service.verifyToken(token, IdentityTokenType.ACCESS));

        assertEquals(IdentityTokenErrorCode.INVALID_ISSUER, exception.getErrorCode());
    }

    @Test
    void verifyToken_WhenTokenTypeIsInvalid_ShouldThrowException() {
        IdentityTokenClaims claims = new IdentityTokenClaims(
            userId,
            IdentityTokenType.ACCESS,
            "issuer",
            Instant.now().minusSeconds(100),
            Instant.now().plusSeconds(1000)
        );
        when(properties.getIssuer()).thenReturn("issuer");
        when(identityTokenProvider.decode(token)).thenReturn(claims);

        UnauthenticatedException exception =
            assertThrows(UnauthenticatedException.class, () -> service.verifyToken(token, IdentityTokenType.REFRESH));

        assertEquals(IdentityTokenErrorCode.INVALID_TOKEN_TYPE, exception.getErrorCode());
    }

    @Test
    void verifyToken_WhenTokenIsExpired_ShouldThrowException() {
        IdentityTokenClaims claims = new IdentityTokenClaims(
            userId,
            IdentityTokenType.ACCESS,
            "issuer",
            Instant.now().minusSeconds(1000),
            Instant.now().minusSeconds(100)
        );
        when(properties.getIssuer()).thenReturn("issuer");
        when(identityTokenProvider.decode(token)).thenReturn(claims);

        UnauthenticatedException exception =
            assertThrows(UnauthenticatedException.class, () -> service.verifyToken(token, IdentityTokenType.ACCESS));

        assertEquals(IdentityTokenErrorCode.TOKEN_EXPIRED, exception.getErrorCode());
    }
}