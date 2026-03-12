package ru.jerael.booktracker.backend.data.service.token;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.jerael.booktracker.backend.data.exception.factory.JwtProviderExceptionFactory;
import ru.jerael.booktracker.backend.data.service.token.config.JwtProperties;
import ru.jerael.booktracker.backend.domain.exception.UnauthenticatedException;
import ru.jerael.booktracker.backend.domain.exception.factory.IdentityTokenExceptionFactory;
import ru.jerael.booktracker.backend.domain.model.auth.GeneratedToken;
import ru.jerael.booktracker.backend.domain.model.auth.IdentityTokenClaims;
import ru.jerael.booktracker.backend.domain.model.auth.IdentityTokenType;
import ru.jerael.booktracker.backend.domain.service.token.IdentityTokenProvider;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtProvider implements IdentityTokenProvider {
    private final JwtProperties properties;
    private final JWSAlgorithm jwsAlgorithm = JWSAlgorithm.HS256;

    @Override
    public GeneratedToken generateToken(Map<String, Object> claims, IdentityTokenType tokenType) {
        Instant now = Instant.now();
        Duration expiry =
            tokenType == IdentityTokenType.ACCESS ? properties.getAccessExpiry() : properties.getRefreshExpiry();
        Instant expiresAt = now.plus(expiry);
        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
            .issuer(properties.getIssuer())
            .issueTime(Date.from(now))
            .expirationTime(Date.from(expiresAt));
        claims.forEach(builder::claim);
        builder.claim("type", tokenType.name());
        String value = signJWT(builder.build());
        return new GeneratedToken(value, expiresAt);
    }

    @Override
    public Map<String, Object> extractClaims(String token, IdentityTokenType expectedType) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier jwsVerifier = new MACVerifier(properties.getSecret());
            if (!signedJWT.verify(jwsVerifier)) {
                throw JwtProviderExceptionFactory.invalidSignature();
            }
            JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();
            if (new Date().after(jwtClaimsSet.getExpirationTime())) {
                throw JwtProviderExceptionFactory.tokenExpired();
            }
            if (!jwtClaimsSet.getIssuer().equals(properties.getIssuer())) {
                throw JwtProviderExceptionFactory.invalidIssuer(jwtClaimsSet.getIssuer());
            }
            String actualType = jwtClaimsSet.getClaim("type").toString();
            if (actualType == null || actualType.isBlank() || !actualType.equals(expectedType.name())) {
                throw JwtProviderExceptionFactory.invalidTokenType(expectedType.name(), actualType);
            }
            return jwtClaimsSet.getClaims();
        } catch (JOSEException e) {
            throw JwtProviderExceptionFactory.signingFailed(e.getMessage(), e);
        } catch (UnauthenticatedException e) {
            throw e;
        } catch (Exception e) {
            throw JwtProviderExceptionFactory.tokenMalformed(e.getMessage());
        }
    }

    @Override
    public String encode(IdentityTokenClaims data) {
        try {
            JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .claim("userId", data.userId().toString())
                .claim("type", data.type().name())
                .claim("issuer", data.issuer())
                .claim("issuedAt", data.issuedAt().getEpochSecond())
                .claim("expiresAt", data.expiresAt().getEpochSecond())
                .build();

            JWSSigner jwsSigner = new MACSigner(properties.getSecret());
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(jwsAlgorithm), jwtClaimsSet);
            signedJWT.sign(jwsSigner);
            return signedJWT.serialize();
        } catch (NullPointerException e) {
            throw IdentityTokenExceptionFactory.tokenMalformed(e.getMessage());
        } catch (Exception e) {
            throw JwtProviderExceptionFactory.signingFailed(e.getMessage(), e);
        }
    }

    @Override
    public IdentityTokenClaims decode(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier jwsVerifier = new MACVerifier(properties.getSecret());
            if (!signedJWT.verify(jwsVerifier)) {
                throw IdentityTokenExceptionFactory.invalidSignature();
            }
            Map<String, Object> claims = signedJWT.getJWTClaimsSet().getClaims();
            return new IdentityTokenClaims(
                UUID.fromString(claims.get("userId").toString()),
                IdentityTokenType.valueOf(claims.get("type").toString()),
                claims.get("issuer").toString(),
                Instant.ofEpochSecond(((Number) claims.get("issuedAt")).longValue()),
                Instant.ofEpochSecond(((Number) claims.get("expiresAt")).longValue())
            );
        } catch (JOSEException e) {
            throw JwtProviderExceptionFactory.signingFailed(e.getMessage(), e);
        } catch (UnauthenticatedException e) {
            throw e;
        } catch (Exception e) {
            throw IdentityTokenExceptionFactory.tokenMalformed(e.getMessage());
        }
    }

    private String signJWT(JWTClaimsSet jwtClaimsSet) {
        try {
            JWSSigner jwsSigner = new MACSigner(properties.getSecret());
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(jwsAlgorithm), jwtClaimsSet);
            signedJWT.sign(jwsSigner);
            return signedJWT.serialize();
        } catch (Exception e) {
            throw JwtProviderExceptionFactory.signingFailed(e.getMessage(), e);
        }
    }
}
