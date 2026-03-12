package ru.jerael.booktracker.backend.application.usecase.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.jerael.booktracker.backend.domain.exception.UnauthenticatedException;
import ru.jerael.booktracker.backend.domain.exception.code.IdentityTokenErrorCode;
import ru.jerael.booktracker.backend.domain.exception.factory.IdentityTokenExceptionFactory;
import ru.jerael.booktracker.backend.domain.hasher.PasswordHasher;
import ru.jerael.booktracker.backend.domain.model.auth.*;
import ru.jerael.booktracker.backend.domain.repository.RefreshTokenRepository;
import ru.jerael.booktracker.backend.domain.service.token.AuthTokenService;
import ru.jerael.booktracker.backend.domain.validator.AuthValidator;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokensUseCaseImplTest {

    @Mock
    private AuthValidator authValidator;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private AuthTokenService authTokenService;

    @InjectMocks
    private RefreshTokensUseCaseImpl useCase;

    private final UUID userId = UUID.fromString("ee39af7a-a073-4473-878a-1aae34e98bb7");
    private final String refreshToken = "user.jwt.token";
    private final RefreshTokenPayload data = new RefreshTokenPayload(refreshToken);
    private final IdentityTokenClaims claims = new IdentityTokenClaims(
        userId,
        IdentityTokenType.REFRESH,
        "issuer",
        Instant.now().plusSeconds(100),
        Instant.now().plusSeconds(1000)
    );
    private final UUID tokenId = UUID.fromString("b4e4e673-b851-4c0e-8626-a073941a018b");

    @Test
    void execute_ShouldDeleteExistingTokenAndInsertNewAndReturnNewTokenPair() {
        RefreshToken token = new RefreshToken(
            tokenId,
            userId,
            "token hash",
            Instant.now().plusSeconds(1000)
        );
        TokenPair tokenPair = new TokenPair("access token", "refresh token");
        when(authTokenService.verifyToken(refreshToken, IdentityTokenType.REFRESH)).thenReturn(claims);
        when(refreshTokenRepository.findAllByUserId(userId)).thenReturn(List.of(token));
        when(passwordHasher.verify(refreshToken, "token hash")).thenReturn(true);
        when(authTokenService.issueTokens(userId)).thenReturn(tokenPair);

        TokenPair result = useCase.execute(data);

        assertEquals(tokenPair, result);
        verify(authValidator).validateRefreshTokenPayload(data);
        verify(refreshTokenRepository).deleteById(tokenId);
        verify(authTokenService).issueTokens(userId);
    }

    @Test
    void execute_WhenTokenVerificationFails_ShouldThrowInvalidCredentials() {
        RefreshTokenPayload payload = new RefreshTokenPayload(refreshToken);
        when(authTokenService.verifyToken(anyString(), eq(IdentityTokenType.REFRESH)))
            .thenThrow(IdentityTokenExceptionFactory.tokenExpired());

        UnauthenticatedException exception =
            assertThrows(UnauthenticatedException.class, () -> useCase.execute(payload));

        assertEquals(IdentityTokenErrorCode.INVALID_TOKEN, exception.getErrorCode());

        verifyNoInteractions(refreshTokenRepository);
        verifyNoInteractions(passwordHasher);
    }

    @Test
    void execute_WhenTokenHashMismatched_ShouldThrowUnauthenticatedException() {
        RefreshToken token = new RefreshToken(tokenId, userId, "token hash 2", Instant.now());
        when(authTokenService.verifyToken(refreshToken, IdentityTokenType.REFRESH)).thenReturn(claims);
        when(refreshTokenRepository.findAllByUserId(userId)).thenReturn(List.of(token));
        when(passwordHasher.verify(refreshToken, "token hash 2")).thenReturn(false);

        assertThrows(UnauthenticatedException.class, () -> useCase.execute(data));

        verify(refreshTokenRepository, never()).deleteById(any());
        verify(authTokenService, never()).issueTokens(any());
    }

    @Test
    void execute_WhenNotFoundTokensForUser_ShouldThrowUnauthenticatedException() {
        when(authTokenService.verifyToken(refreshToken, IdentityTokenType.REFRESH)).thenReturn(claims);
        when(refreshTokenRepository.findAllByUserId(userId)).thenReturn(List.of());

        UnauthenticatedException exception = assertThrows(UnauthenticatedException.class, () -> useCase.execute(data));

        assertEquals(IdentityTokenErrorCode.INVALID_TOKEN, exception.getErrorCode());
    }
}