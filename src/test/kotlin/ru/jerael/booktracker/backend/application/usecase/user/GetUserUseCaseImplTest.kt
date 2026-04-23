package ru.jerael.booktracker.backend.application.usecase.user

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import ru.jerael.booktracker.backend.domain.repository.UserRepository
import ru.jerael.booktracker.backend.factory.user.UserDomainFactory
import java.util.*

@ExtendWith(MockKExtension::class)
class GetUserUseCaseImplTest {
    
    @MockK
    private lateinit var userRepository: UserRepository
    
    @InjectMockKs
    private lateinit var useCase: GetUserUseCaseImpl
    
    @Test
    fun `when user exists, execute should return user`() {
        val userId = UUID.randomUUID()
        val user = UserDomainFactory.createUser(id = userId)
        
        every { userRepository.findById(userId) } returns Optional.of(user)
        
        val result = useCase.execute(userId)
        
        assertEquals(user, result)
        
        verify { userRepository.findById(userId) }
    }
}