package ru.jerael.booktracker.backend.data.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.factory.user.UserDomainFactory
import ru.jerael.booktracker.backend.factory.user.UserEntityFactory

class UserDataMapperTest {
    private val mapper = UserDataMapper()
    
    @Test
    fun `toDomain should map UserEntity to User`() {
        val entity = UserEntityFactory.createUserEntity()
        
        val user = mapper.toDomain(entity)
        
        with(user) {
            assertEquals(entity.id, id)
            assertEquals(entity.email, email)
            assertEquals(entity.passwordHash, passwordHash)
            assertEquals(entity.isVerified, isVerified)
            assertEquals(entity.createdAt, createdAt)
        }
    }
    
    @Test
    fun `toEntity should map User to UserEntity`() {
        val user = UserDomainFactory.createUser()
        
        val entity = mapper.toEntity(user)
        
        with(entity) {
            assertEquals(user.id, id)
            assertEquals(user.email, email)
            assertEquals(user.passwordHash, passwordHash)
            assertEquals(user.isVerified, isVerified)
            assertEquals(user.createdAt, createdAt)
        }
    }
}