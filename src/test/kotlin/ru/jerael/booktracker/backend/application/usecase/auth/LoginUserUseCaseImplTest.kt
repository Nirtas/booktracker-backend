package ru.jerael.booktracker.backend.application.usecase.auth

import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import ru.jerael.booktracker.backend.domain.exception.UnauthenticatedException
import ru.jerael.booktracker.backend.domain.exception.ValidationException
import ru.jerael.booktracker.backend.domain.hasher.PasswordHasher
import ru.jerael.booktracker.backend.domain.repository.UserRepository
import ru.jerael.booktracker.backend.domain.service.token.AuthTokenService
import ru.jerael.booktracker.backend.domain.validator.AuthValidator
import ru.jerael.booktracker.backend.factory.auth.AuthDomainFactory
import ru.jerael.booktracker.backend.factory.user.UserDomainFactory
import java.util.*

@ExtendWith(MockKExtension::class)
class LoginUserUseCaseImplTest {
    
    @MockK(relaxed = true)
    private lateinit var authValidator: AuthValidator
    
    @MockK
    private lateinit var userRepository: UserRepository
    
    @MockK
    private lateinit var passwordHasher: PasswordHasher
    
    @MockK
    private lateinit var authTokenService: AuthTokenService
    
    @InjectMockKs
    private lateinit var useCase: LoginUserUseCaseImpl
    
    @Test
    fun `execute should return TokenPair`() {
        val userLogin = AuthDomainFactory.createUserLogin()
        val user = UserDomainFactory.createUser(email = userLogin.email, isVerified = true)
        val tokenPair = AuthDomainFactory.createTokenPair()
        
        every { userRepository.findByEmail(userLogin.email) } returns Optional.of(user)
        every { passwordHasher.verify(any(), user.passwordHash) } returns true
        every { authTokenService.issueTokens(user.id) } returns tokenPair
        
        val result = useCase.execute(userLogin)
        
        assertEquals(tokenPair, result)
        
        verify { authValidator.validateLogin(userLogin) }
    }
    
    @Test
    fun `when user not found, execute should throw UnauthenticatedException`() {
        val userLogin = AuthDomainFactory.createUserLogin()
        
        every { userRepository.findByEmail(userLogin.email) } returns Optional.empty()
        
        assertThrows(UnauthenticatedException::class.java) { useCase.execute(userLogin) }
        
        verify { passwordHasher wasNot called }
        verify { authTokenService wasNot called }
    }
    
    @Test
    fun `when password is invalid, execute should throw UnauthenticatedException`() {
        val userLogin = AuthDomainFactory.createUserLogin()
        val user = UserDomainFactory.createUser(email = userLogin.email, isVerified = true)
        
        every { userRepository.findByEmail(userLogin.email) } returns Optional.of(user)
        every { passwordHasher.verify(any(), user.passwordHash) } returns false
        
        assertThrows(UnauthenticatedException::class.java) { useCase.execute(userLogin) }
        
        verify { authTokenService wasNot called }
    }
    
    @Test
    fun `when user not verified, execute should throw ValidationException`() {
        val userLogin = AuthDomainFactory.createUserLogin()
        val user = UserDomainFactory.createUser(email = userLogin.email, isVerified = false)
        
        every { userRepository.findByEmail(userLogin.email) } returns Optional.of(user)
        every { passwordHasher.verify(any(), user.passwordHash) } returns true
        
        assertThrows(ValidationException::class.java) { useCase.execute(userLogin) }
        
        verify { authTokenService wasNot called }
    }
}