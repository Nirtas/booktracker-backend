package ru.jerael.booktracker.backend.data.repository

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.context.annotation.Import
import ru.jerael.booktracker.backend.data.db.repository.JpaEmailVerificationRepository
import ru.jerael.booktracker.backend.data.db.repository.JpaUserRepository
import ru.jerael.booktracker.backend.data.mapper.EmailVerificationDataMapper
import ru.jerael.booktracker.backend.factory.user.UserEntityFactory
import ru.jerael.booktracker.backend.factory.verification.EmailVerificationDomainFactory

@DataJpaTest
@Import(EmailVerificationRepositoryImpl::class, EmailVerificationDataMapper::class)
class EmailVerificationRepositoryImplTest {
    
    @Autowired
    private lateinit var jpaEmailVerificationRepository: JpaEmailVerificationRepository
    
    @Autowired
    private lateinit var jpaUserRepository: JpaUserRepository
    
    @Autowired
    private lateinit var emailVerificationRepository: EmailVerificationRepositoryImpl
    
    @Test
    fun `deleteByUserIdAndType should delete email verification by userId and type`() {
        val user = jpaUserRepository.save(UserEntityFactory.createUserEntity())
        val emailVerification = EmailVerificationDomainFactory.createEmailVerification(id = null, userId = user.id)
        
        val savedVerification = emailVerificationRepository.save(emailVerification)
        
        assertNotNull(savedVerification.id)
        assertTrue(jpaEmailVerificationRepository.existsById(savedVerification.id))
        
        emailVerificationRepository.deleteByUserIdAndType(user.id, emailVerification.type)
        
        assertFalse(jpaEmailVerificationRepository.existsById(savedVerification.id))
    }
    
    @Test
    fun `when verification exists, findByUserIdAndType should return EmailVerification`() {
        val user = jpaUserRepository.save(UserEntityFactory.createUserEntity())
        val emailVerification = EmailVerificationDomainFactory.createEmailVerification(id = null, userId = user.id)
        
        val savedVerification = emailVerificationRepository.save(emailVerification)
        
        assertNotNull(savedVerification.id)
        assertTrue(jpaEmailVerificationRepository.existsById(savedVerification.id))
        
        val result = emailVerificationRepository.findByUserIdAndType(user.id, emailVerification.type)
        
        with(result.get()) {
            assertEquals(user.id, userId)
            assertEquals(emailVerification.email, this.email)
            assertEquals(emailVerification.type, this.type)
        }
    }
    
    @Test
    fun `when id is null, save should insert new EmailVerification`() {
        val user = jpaUserRepository.save(UserEntityFactory.createUserEntity())
        val emailVerification = EmailVerificationDomainFactory.createEmailVerification(id = null, userId = user.id)
        
        val result = emailVerificationRepository.save(emailVerification)
        
        with(result) {
            assertNotNull(id)
            assertTrue(jpaEmailVerificationRepository.existsById(id))
            assertEquals(user.id, userId)
            assertEquals(emailVerification.email, this.email)
            assertEquals(emailVerification.type, this.type)
        }
    }
}