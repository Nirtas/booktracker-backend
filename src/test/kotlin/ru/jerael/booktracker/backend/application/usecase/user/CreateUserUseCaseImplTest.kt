package ru.jerael.booktracker.backend.application.usecase.user

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
import ru.jerael.booktracker.backend.domain.exception.AlreadyExistsException
import ru.jerael.booktracker.backend.domain.hasher.PasswordHasher
import ru.jerael.booktracker.backend.domain.model.verification.VerificationType
import ru.jerael.booktracker.backend.domain.repository.UserRepository
import ru.jerael.booktracker.backend.domain.validator.UserValidator
import ru.jerael.booktracker.backend.domain.verification.EmailVerificationService
import ru.jerael.booktracker.backend.factory.user.UserDomainFactory
import java.time.Instant
import java.util.*

@ExtendWith(MockKExtension::class)
class CreateUserUseCaseImplTest {
    
    @MockK
    private lateinit var userRepository: UserRepository
    
    @MockK
    private lateinit var passwordHasher: PasswordHasher
    
    @MockK(relaxed = true)
    private lateinit var userValidator: UserValidator
    
    @MockK
    private lateinit var emailVerificationService: EmailVerificationService
    
    @InjectMockKs
    private lateinit var useCase: CreateUserUseCaseImpl
    
    @Test
    fun `when email is not taken, execute should create user and initiate verification`() {
        val userCreation = UserDomainFactory.createUserCreation()
        val passwordHash = "password hash"
        val user = UserDomainFactory.createUser(
            email = userCreation.email,
            passwordHash = passwordHash
        )
        val expiresAt = Instant.now().plusSeconds(600)
        
        every { userRepository.findByEmail(userCreation.email) } returns Optional.empty()
        every { passwordHasher.hash(userCreation.password) } returns passwordHash
        every { userRepository.save(any()) } returns user
        every { emailVerificationService.initiate(any()) } returns expiresAt
        
        with(useCase.execute(userCreation)) {
            assertEquals(user.id, userId)
            assertEquals(userCreation.email, email)
            assertEquals(expiresAt, this.expiresAt)
        }
        
        verify {
            userValidator.validateCreation(userCreation)
            userRepository.findByEmail(userCreation.email)
            passwordHasher.hash(userCreation.password)
            userRepository.save(any())
            emailVerificationService.initiate(match {
                it.userId == user.id && it.email == userCreation.email && it.type == VerificationType.REGISTRATION
            })
        }
    }
    
    @Test
    fun `when email is taken, execute should throw AlreadyExistsException`() {
        val userCreation = UserDomainFactory.createUserCreation()
        val user = UserDomainFactory.createUser(email = userCreation.email)
        
        every { userRepository.findByEmail(userCreation.email) } returns Optional.of(user)
        
        assertThrows(AlreadyExistsException::class.java) { useCase.execute(userCreation) }
        
        verify {
            userValidator.validateCreation(userCreation)
            userRepository.findByEmail(userCreation.email)
            passwordHasher wasNot called
            emailVerificationService wasNot called
        }
    }
}