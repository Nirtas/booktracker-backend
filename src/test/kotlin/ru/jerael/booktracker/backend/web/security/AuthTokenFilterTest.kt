package ru.jerael.booktracker.backend.web.security

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.servlet.HandlerExceptionResolver
import ru.jerael.booktracker.backend.domain.service.token.AuthTokenService
import ru.jerael.booktracker.backend.factory.auth.AuthDomainFactory

@ExtendWith(MockKExtension::class)
class AuthTokenFilterTest {
    private val authTokenService = mockk<AuthTokenService>()
    private val httpServletRequest = mockk<HttpServletRequest>()
    private val httpServletResponse = mockk<HttpServletResponse>()
    private val filterChain = mockk<FilterChain>(relaxed = true)
    private val resolver = mockk<HandlerExceptionResolver>(relaxed = true)
    
    @InjectMockKs
    private lateinit var filter: AuthTokenFilter
    
    @BeforeEach
    fun setUp() {
        SecurityContextHolder.clearContext()
    }
    
    @Test
    fun `when token is valid, doFilterInternal should set Authentication`() {
        val claims = AuthDomainFactory.createIdentityTokenClaims()
        val token = "token"
        every { httpServletRequest.getHeader("Authorization") } returns "Bearer $token"
        every { authTokenService.authenticateToken(token, any()) } returns claims
        
        filter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain)
        
        val auth = SecurityContextHolder.getContext().authentication
        assertNotNull(auth)
        assertEquals(claims.userId, auth.principal)
    }
    
    @Test
    fun `when token is not provided, doFilterInternal should not set Authentication`() {
        filter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain)
        
        val auth = SecurityContextHolder.getContext().authentication
        assertNull(auth)
    }
}