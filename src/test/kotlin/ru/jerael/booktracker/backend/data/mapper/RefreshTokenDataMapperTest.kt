package ru.jerael.booktracker.backend.data.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.factory.auth.AuthDomainFactory
import ru.jerael.booktracker.backend.factory.auth.AuthEntityFactory
import java.util.*

class RefreshTokenDataMapperTest {
    private val mapper = RefreshTokenDataMapper()
    
    @Test
    fun `toEntity should map RefreshToken to RefreshTokenEntity`() {
        val domain = AuthDomainFactory.createRefreshToken()
        
        val entity = mapper.toEntity(domain)
        
        with(entity) {
            assertEquals(domain.id, id)
            assertEquals(domain.userId, user.id)
            assertEquals(domain.tokenHash, tokenHash)
            assertEquals(domain.expiresAt, expiresAt)
        }
    }
    
    @Test
    fun `toDomain should map RefreshTokenEntity to RefreshToken`() {
        val entity = AuthEntityFactory.createRefreshTokenEntity(id = UUID.randomUUID())
        
        val domain = mapper.toDomain(entity)
        
        with(domain) {
            assertEquals(entity.id, id)
            assertEquals(entity.user.id, userId)
            assertEquals(entity.tokenHash, tokenHash)
            assertEquals(entity.expiresAt, expiresAt)
        }
    }
}