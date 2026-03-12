package ru.jerael.booktracker.backend.application.usecase.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.jerael.booktracker.backend.domain.exception.UnauthenticatedException;
import ru.jerael.booktracker.backend.domain.exception.ValidationException;
import ru.jerael.booktracker.backend.domain.exception.factory.IdentityTokenExceptionFactory;
import ru.jerael.booktracker.backend.domain.model.auth.RefreshTokenPayload;
import ru.jerael.booktracker.backend.domain.model.auth.TokenPair;
import ru.jerael.booktracker.backend.domain.service.token.AuthTokenService;
import ru.jerael.booktracker.backend.domain.validator.AuthValidator;
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
    private AuthTokenService authTokenService;

    @InjectMocks
    private RefreshTokensUseCaseImpl useCase;

    private final UUID userId = UUID.fromString("ee39af7a-a073-4473-878a-1aae34e98bb7");
    private final String refreshToken = "user.jwt.token";
    private final RefreshTokenPayload data = new RefreshTokenPayload(refreshToken);

    @Test
    void execute_WhenPayloadIsValid_ShouldRevokeOldAndReturnNewTokens() {
        TokenPair tokenPair = new TokenPair("access token", "refresh token");
        when(authTokenService.revokeToken(refreshToken)).thenReturn(userId);
        when(authTokenService.issueTokens(userId)).thenReturn(tokenPair);

        TokenPair result = useCase.execute(data);

        assertEquals(tokenPair, result);
        verify(authValidator).validateRefreshTokenPayload(data);
        verify(authTokenService).revokeToken(refreshToken);
        verify(authTokenService).issueTokens(userId);
    }

    @Test
    void execute_WhenValidationFails_ShouldThrowException() {
        doThrow(new ValidationException(List.of())).when(authValidator).validateRefreshTokenPayload(data);

        assertThrows(ValidationException.class, () -> useCase.execute(data));

        verifyNoInteractions(authTokenService);
    }

    @Test
    void execute_WhenRevokeFails_ShouldThrowException() {
        when(authTokenService.revokeToken(refreshToken)).thenThrow(IdentityTokenExceptionFactory.invalidToken());

        assertThrows(UnauthenticatedException.class, () -> useCase.execute(data));

        verify(authTokenService, never()).issueTokens(any());
    }
}