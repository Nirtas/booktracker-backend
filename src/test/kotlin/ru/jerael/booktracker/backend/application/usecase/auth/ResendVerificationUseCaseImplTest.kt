package ru.jerael.booktracker.backend.application.usecase.auth

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import ru.jerael.booktracker.backend.domain.exception.NotFoundException
import ru.jerael.booktracker.backend.domain.exception.ValidationException
import ru.jerael.booktracker.backend.domain.model.verification.EmailVerificationInitiation
import ru.jerael.booktracker.backend.domain.model.verification.VerificationType
import ru.jerael.booktracker.backend.domain.repository.UserRepository
import ru.jerael.booktracker.backend.domain.validator.AuthValidator
import ru.jerael.booktracker.backend.domain.verification.EmailVerificationService
import ru.jerael.booktracker.backend.factory.auth.AuthDomainFactory
import ru.jerael.booktracker.backend.factory.user.UserDomainFactory
import java.time.Instant
import java.util.*

@ExtendWith(MockKExtension::class)
class ResendVerificationUseCaseImplTest {
    
    @MockK(relaxed = true)
    private lateinit var authValidator: AuthValidator
    
    @MockK
    private lateinit var userRepository: UserRepository
    
    @MockK
    private lateinit var emailVerificationService: EmailVerificationService
    
    @InjectMockKs
    private lateinit var useCase: ResendVerificationUseCaseImpl
    
    @Test
    fun `execute should return ResendVerificationResult`() {
        val user = UserDomainFactory.createUser(isVerified = false)
        
        val userId = user.id
        val expiresAt = Instant.now().plusSeconds(600)
        
        val data = AuthDomainFactory.createResendVerification(userId = userId)
        
        every { userRepository.findById(userId) } returns Optional.of(user)
        every { emailVerificationService.initiate(any()) } returns expiresAt
        
        with(useCase.execute(data)) {
            assertEquals(userId, this.userId)
            assertEquals(user.email, this.email)
            assertEquals(expiresAt, this.expiresAt)
        }
        
        verify { authValidator.validateResendVerification(data) }
        
        val emailVerificationInitiationSlot = slot<EmailVerificationInitiation>()
        verify { emailVerificationService.initiate(capture(emailVerificationInitiationSlot)) }
        
        with(emailVerificationInitiationSlot.captured) {
            assertEquals(userId, this.userId)
            assertEquals(user.email, this.email)
            assertEquals(VerificationType.REGISTRATION, this.type)
        }
    }
    
    @Test
    fun `when user not found, execute should throw NotFoundException`() {
        val data = AuthDomainFactory.createResendVerification()
        
        every { userRepository.findById(data.userId) } returns Optional.empty()
        
        assertThrows(NotFoundException::class.java) { useCase.execute(data) }
        
        verify(exactly = 0) { emailVerificationService.initiate(any()) }
    }
    
    @Test
    fun `when user already verified, execute should throw ValidationException`() {
        val user = UserDomainFactory.createUser(isVerified = true)
        val data = AuthDomainFactory.createResendVerification(userId = user.id)
        
        every { userRepository.findById(user.id) } returns Optional.of(user)
        
        assertThrows(ValidationException::class.java) { useCase.execute(data) }
        
        verify(exactly = 0) { emailVerificationService.initiate(any()) }
    }
}