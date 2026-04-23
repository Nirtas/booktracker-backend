package ru.jerael.booktracker.backend.web.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.factory.user.UserDomainFactory
import ru.jerael.booktracker.backend.factory.user.UserWebFactory

class UserWebMapperTest {
    private val mapper = UserWebMapper()
    
    @Test
    fun `toResponse should map UserCreationResult to UserCreationResponse`() {
        val result = UserDomainFactory.createUserCreationResult()
        
        val response = mapper.toResponse(result)
        
        with(response) {
            assertEquals(result.userId, userId)
            assertEquals(result.email, email)
            assertEquals(result.expiresAt, expiresAt)
        }
    }
    
    @Test
    fun `toDomain should map UserCreationRequest to UserCreation`() {
        val request = UserWebFactory.createUserCreationRequest()
        
        val domain = mapper.toDomain(request)
        
        with(domain) {
            assertEquals(request.email, email)
            assertEquals(request.password, password)
        }
    }
    
    @Test
    fun `toResponse should map User to UserResponse`() {
        val user = UserDomainFactory.createUser()
        
        val response = mapper.toResponse(user)
        
        with(response) {
            assertEquals(user.id, userId)
            assertEquals(user.email, email)
            assertEquals(user.isVerified, isVerified)
            assertEquals(user.createdAt, createdAt)
        }
    }
}