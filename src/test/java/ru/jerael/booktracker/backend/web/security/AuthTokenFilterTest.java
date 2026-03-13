package ru.jerael.booktracker.backend.web.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.jerael.booktracker.backend.domain.model.auth.IdentityTokenClaims;
import ru.jerael.booktracker.backend.domain.model.auth.IdentityTokenType;
import ru.jerael.booktracker.backend.domain.service.token.AuthTokenService;
import java.time.Instant;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthTokenFilterTest {

    @Mock
    private AuthTokenService authTokenService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private AuthTokenFilter filter;

    private final UUID userId = UUID.fromString("ee39af7a-a073-4473-878a-1aae34e98bb7");
    private final String token = "token";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_WhenValidToken_ShouldSetAuthentication() throws Exception {
        IdentityTokenClaims claims = new IdentityTokenClaims(
            userId,
            IdentityTokenType.ACCESS,
            "issuer",
            Instant.now().minusSeconds(100),
            Instant.now().plusSeconds(1000)
        );
        when(httpServletRequest.getHeader(any())).thenReturn("Bearer " + token);
        when(authTokenService.authenticateToken(token, IdentityTokenType.ACCESS)).thenReturn(claims);

        filter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(userId, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @Test
    void doFilterInternal_WhenNoToken_ShouldNotSetAuthentication() throws Exception {
        filter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}