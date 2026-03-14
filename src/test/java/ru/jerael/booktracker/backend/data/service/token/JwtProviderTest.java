package ru.jerael.booktracker.backend.data.service.token;

import org.junit.jupiter.api.Test;
import ru.jerael.booktracker.backend.data.service.token.config.JwtProperties;
import ru.jerael.booktracker.backend.domain.exception.UnauthenticatedException;
import ru.jerael.booktracker.backend.domain.model.auth.IdentityTokenClaims;
import ru.jerael.booktracker.backend.domain.model.auth.IdentityTokenType;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtProviderTest {
    private final JwtProvider jwtProvider;

    private final String secret = "very-very-very-very-very-long-string";
    private final Instant issuedAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    private final Duration accessExpiry = Duration.ofMinutes(10L);
    private final Instant accessExpiresAt = issuedAt.plus(accessExpiry);
    private final Duration refreshExpiry = Duration.ofDays(30L);
    private final Instant refreshExpiresAt = issuedAt.plus(refreshExpiry);
    private final String issuer = "issuer";

    private final UUID userId = UUID.fromString("ee39af7a-a073-4473-878a-1aae34e98bb7");
    private final IdentityTokenClaims claims2 = new IdentityTokenClaims(
        userId,
        IdentityTokenType.ACCESS,
        issuer,
        issuedAt,
        accessExpiresAt
    );

    public JwtProviderTest() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret(secret);
        jwtProvider = new JwtProvider(properties);
    }

    @Test
    void encode_ShouldCreateValidSignedJwt() {
        String token = jwtProvider.encode(claims2);

        assertThat(token).isNotBlank();
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void decode_WhenTokenIsValid_ShouldReturnCorrectClaims() {
        String token = jwtProvider.encode(claims2);

        IdentityTokenClaims result = jwtProvider.decode(token);

        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.issuer()).isEqualTo(issuer);
        assertThat(result.type()).isEqualTo(IdentityTokenType.ACCESS);
        assertThat(result.issuedAt()).isEqualTo(issuedAt);
        assertThat(result.expiresAt()).isEqualTo(accessExpiresAt);
    }

    @Test
    void decode_WhenSignatureIsInvalid_ShouldThrowException() {
        String validToken = jwtProvider.encode(claims2);
        String invalidToken = validToken.substring(0, validToken.length() - 10);

        Throwable throwable = assertThrows(UnauthenticatedException.class, () -> jwtProvider.decode(invalidToken));

        assertThat(throwable.getMessage()).contains("Token signature is invalid");
    }

    @Test
    void decode_WhenTokenIsMalformed_ShouldThrowException() {
        String malformedToken = "not a jwt string";

        Throwable throwable = assertThrows(UnauthenticatedException.class, () -> jwtProvider.decode(malformedToken));

        assertThat(throwable.getMessage()).contains("Token is invalid or corrupted");
    }
}