package ru.jerael.booktracker.backend.data.service.token;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.Test;
import ru.jerael.booktracker.backend.data.service.token.config.JwtProperties;
import ru.jerael.booktracker.backend.domain.exception.UnauthenticatedException;
import ru.jerael.booktracker.backend.domain.model.auth.GeneratedToken;
import ru.jerael.booktracker.backend.domain.model.auth.IdentityTokenType;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtProviderTest {
    private final JwtProvider jwtProvider;

    private final String secret = "very-very-very-very-very-long-string";
    private final Duration accessExpiry = Duration.ofMinutes(10L);
    private final Duration refreshExpiry = Duration.ofDays(30L);
    private final String issuer = "issuer";

    private final UUID userId = UUID.fromString("ee39af7a-a073-4473-878a-1aae34e98bb7");
    private final Map<String, Object> claims = Map.of("sub", userId);

    public JwtProviderTest() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret(secret);
        properties.setAccessExpiry(accessExpiry);
        properties.setRefreshExpiry(refreshExpiry);
        properties.setIssuer(issuer);
        jwtProvider = new JwtProvider(properties);
    }

    @Test
    void generateToken_ShouldCreateValidSignedJwt() {
        String token = jwtProvider.generateToken(claims, IdentityTokenType.ACCESS).value();

        assertThat(token).isNotBlank();
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void generateToken_WhenTypeIsAccess_ShouldSetAccessExpiry() {
        Instant now = Instant.now();
        Instant expectedExpiry = now.plus(accessExpiry);

        GeneratedToken generatedToken = jwtProvider.generateToken(claims, IdentityTokenType.ACCESS);

        assertThat(generatedToken.expiresAt()).isCloseTo(expectedExpiry, within(1, ChronoUnit.SECONDS));
    }

    @Test
    void generateToken_WhenTypeIsRefresh_ShouldSetRefreshExpiry() {
        Instant now = Instant.now();
        Instant expectedExpiry = now.plus(refreshExpiry);

        GeneratedToken generatedToken = jwtProvider.generateToken(claims, IdentityTokenType.REFRESH);

        assertThat(generatedToken.expiresAt()).isCloseTo(expectedExpiry, within(1, ChronoUnit.SECONDS));
    }

    @Test
    void extractClaims_WhenTokenIsValid_ShouldReturnCorrectClaims() {
        GeneratedToken generatedToken = jwtProvider.generateToken(claims, IdentityTokenType.ACCESS);

        Map<String, Object> result = jwtProvider.extractClaims(generatedToken.value());

        assertThat(result.get("sub")).isEqualTo(userId.toString());
        assertThat(result.get("iss")).isEqualTo(issuer);
        assertThat(result.get("type")).isEqualTo(IdentityTokenType.ACCESS.name());
    }

    @Test
    void extractClaims_WhenSignatureIsInvalid_ShouldThrowException() {
        String validToken = jwtProvider.generateToken(claims, IdentityTokenType.ACCESS).value();
        String invalidToken = validToken.substring(0, validToken.length() - 10);

        Throwable throwable =
            assertThrows(UnauthenticatedException.class, () -> jwtProvider.extractClaims(invalidToken));

        assertThat(throwable.getMessage()).contains("Token signature is invalid");
    }

    @Test
    void extractClaims_WhenTokenIsExpired_ShouldThrowException() throws Exception {
        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
            .expirationTime(Date.from(Instant.now().minusSeconds(10)));
        claims.forEach(builder::claim);

        JWTClaimsSet jwtClaimsSet = builder.build();

        JWSSigner jwsSigner = new MACSigner(secret);
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), jwtClaimsSet);
        signedJWT.sign(jwsSigner);

        String expiredToken = signedJWT.serialize();

        Throwable throwable =
            assertThrows(UnauthenticatedException.class, () -> jwtProvider.extractClaims(expiredToken));

        assertThat(throwable.getMessage()).contains("Token has expired");
    }

    @Test
    void extractClaims_WhenIssuerIsInvalid_ShouldThrowException() throws Exception {
        String wrongIssuer = "wrong issuer";

        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
            .issuer(wrongIssuer)
            .expirationTime(Date.from(Instant.now().plus(accessExpiry)));
        claims.forEach(builder::claim);

        JWTClaimsSet jwtClaimsSet = builder.build();

        JWSSigner jwsSigner = new MACSigner(secret);
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), jwtClaimsSet);
        signedJWT.sign(jwsSigner);

        String badToken = signedJWT.serialize();

        Throwable throwable =
            assertThrows(UnauthenticatedException.class, () -> jwtProvider.extractClaims(badToken));

        assertThat(throwable.getMessage()).contains("Token issuer is invalid: " + wrongIssuer);
    }

    @Test
    void extractClaims_WhenTokenIsMalformed_ShouldThrowException() {
        String malformedToken = "not a jwt string";

        Throwable throwable =
            assertThrows(UnauthenticatedException.class, () -> jwtProvider.extractClaims(malformedToken));

        assertThat(throwable.getMessage()).contains("Token is invalid or corrupted");
    }
}