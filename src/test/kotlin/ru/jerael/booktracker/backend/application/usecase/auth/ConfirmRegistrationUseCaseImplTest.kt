package ru.jerael.booktracker.backend.application.usecase.auth

import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import ru.jerael.booktracker.backend.domain.exception.NotFoundException
import ru.jerael.booktracker.backend.domain.exception.ValidationException
import ru.jerael.booktracker.backend.domain.model.user.User
import ru.jerael.booktracker.backend.domain.model.verification.EmailVerificationConfirmation
import ru.jerael.booktracker.backend.domain.model.verification.VerificationType
import ru.jerael.booktracker.backend.domain.repository.UserRepository
import ru.jerael.booktracker.backend.domain.service.token.AuthTokenService
import ru.jerael.booktracker.backend.domain.validator.AuthValidator
import ru.jerael.booktracker.backend.domain.verification.EmailVerificationService
import ru.jerael.booktracker.backend.factory.auth.AuthDomainFactory
import ru.jerael.booktracker.backend.factory.user.UserDomainFactory
import java.util.*

@ExtendWith(MockKExtension::class)
class ConfirmRegistrationUseCaseImplTest {
    
    @MockK(relaxed = true)
    private lateinit var authValidator: AuthValidator
    
    @MockK
    private lateinit var userRepository: UserRepository
    
    @MockK
    private lateinit var emailVerificationService: EmailVerificationService
    
    @MockK
    private lateinit var authTokenService: AuthTokenService
    
    @InjectMockKs
    private lateinit var useCase: ConfirmRegistrationUseCaseImpl
    
    @Test
    fun `execute should verify user, delete code and return TokenPair`() {
        val unverifiedUser = UserDomainFactory.createUser(isVerified = false)
        
        val userId = unverifiedUser.id
        
        val tokenPair = AuthDomainFactory.createTokenPair()
        val confirmRegistration = AuthDomainFactory.createConfirmRegistration(userId = userId)
        
        every { userRepository.findById(userId) } returns Optional.of(unverifiedUser)
        every { emailVerificationService.confirm(any()) } returns mockk()
        every { userRepository.save(any()) } returns mockk()
        every { authTokenService.issueTokens(userId) } returns tokenPair
        
        val result = useCase.execute(confirmRegistration)
        
        verify { authValidator.validateRegistrationConfirmation(confirmRegistration) }
        
        val emailVerificationConfirmationSlot = slot<EmailVerificationConfirmation>()
        verify { emailVerificationService.confirm(capture(emailVerificationConfirmationSlot)) }
        
        with(emailVerificationConfirmationSlot.captured) {
            assertEquals(unverifiedUser.id, userId)
            assertEquals(confirmRegistration.token, token)
            assertEquals(VerificationType.REGISTRATION, type)
        }
        
        val userSlot = slot<User>()
        verify { userRepository.save(capture(userSlot)) }
        
        assertTrue(userSlot.captured.isVerified)
        
        assertEquals(tokenPair, result)
    }
    
    @Test
    fun `when user not found, execute should throw NotFoundException`() {
        val confirmRegistration = AuthDomainFactory.createConfirmRegistration()
        
        every { userRepository.findById(any()) } returns Optional.empty()
        
        assertThrows(NotFoundException::class.java) { useCase.execute(confirmRegistration) }
    }
    
    @Test
    fun `when user already verified, execute should throw ValidationException`() {
        val verifiedUser = UserDomainFactory.createUser(isVerified = true)
        val confirmRegistration = AuthDomainFactory.createConfirmRegistration()
        
        every { userRepository.findById(any()) } returns Optional.of(verifiedUser)
        
        assertThrows(ValidationException::class.java) { useCase.execute(confirmRegistration) }
        
        verify { emailVerificationService wasNot called }
        verify { authTokenService wasNot called }
    }
}