package ru.jerael.booktracker.backend.data.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.factory.verification.EmailVerificationDomainFactory
import ru.jerael.booktracker.backend.factory.verification.EmailVerificationEntityFactory

class EmailVerificationDataMapperTest {
    private val mapper = EmailVerificationDataMapper()
    
    @Test
    fun `toDomain should map EmailVerificationEntity to EmailVerification`() {
        val entity = EmailVerificationEntityFactory.createEmailVerificationEntity()
        
        val domain = mapper.toDomain(entity)
        
        with(domain) {
            assertEquals(entity.id, id)
            assertEquals(entity.user.id, userId)
            assertEquals(entity.email, email)
            assertEquals(entity.type, type)
            assertEquals(entity.token, token)
            assertEquals(entity.expiresAt, expiresAt)
            assertEquals(entity.createdAt, createdAt)
        }
    }
    
    @Test
    fun `toEntity should map EmailVerification to EmailVerificationEntity`() {
        val domain = EmailVerificationDomainFactory.createEmailVerification()
        
        val entity = mapper.toEntity(domain)
        
        with(entity) {
            assertEquals(domain.id, id)
            assertEquals(domain.userId, user.id)
            assertEquals(domain.email, email)
            assertEquals(domain.type, type)
            assertEquals(domain.token, token)
            assertEquals(domain.expiresAt, expiresAt)
            assertEquals(domain.createdAt, createdAt)
        }
    }
}