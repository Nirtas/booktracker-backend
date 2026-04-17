package ru.jerael.booktracker.backend.data.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.context.annotation.Import
import ru.jerael.booktracker.backend.data.db.repository.JpaRefreshTokenRepository
import ru.jerael.booktracker.backend.data.db.repository.JpaUserRepository
import ru.jerael.booktracker.backend.data.mapper.RefreshTokenDataMapper
import ru.jerael.booktracker.backend.factory.auth.AuthDomainFactory
import ru.jerael.booktracker.backend.factory.auth.AuthEntityFactory
import ru.jerael.booktracker.backend.factory.user.UserEntityFactory

@DataJpaTest
@Import(RefreshTokenRepositoryImpl::class, RefreshTokenDataMapper::class)
class RefreshTokenRepositoryImplTest {
    
    @Autowired
    private lateinit var jpaUserRepository: JpaUserRepository
    
    @Autowired
    private lateinit var jpaRefreshTokenRepository: JpaRefreshTokenRepository
    
    @Autowired
    private lateinit var refreshTokenRepository: RefreshTokenRepositoryImpl
    
    @Test
    fun `save should insert new RefreshToken`() {
        val user = jpaUserRepository.save(UserEntityFactory.createUserEntity())
        val token = AuthDomainFactory.createRefreshToken(id = null, userId = user.id)
        
        refreshTokenRepository.save(token)
        
        val entities = jpaRefreshTokenRepository.findAllByUserId(user.id)
        
        assertEquals(1, entities.size)
        with(entities[0]) {
            assertEquals(user.id, user.id)
            assertEquals(token.tokenHash, tokenHash)
            assertEquals(token.expiresAt, expiresAt)
        }
    }
    
    @Test
    fun `when tokens exists, findAllByUserId should return all user's refreshTokens`() {
        val user1 = jpaUserRepository.save(UserEntityFactory.createUserEntity(email = "user1@example.com"))
        val user2 = jpaUserRepository.save(UserEntityFactory.createUserEntity(email = "user2@example.com"))
        
        jpaRefreshTokenRepository.saveAll(
            listOf(
                AuthEntityFactory.createRefreshTokenEntity(user = user1, tokenHash = "token hash 1"),
                AuthEntityFactory.createRefreshTokenEntity(user = user1, tokenHash = "token hash 2"),
                AuthEntityFactory.createRefreshTokenEntity(user = user2, tokenHash = "token hash 3")
            )
        )
        
        val result = refreshTokenRepository.findAllByUserId(user1.id)
        
        with(result) {
            assertEquals(2, size)
            assertThat(this).extracting("tokenHash").containsExactlyInAnyOrder("token hash 1", "token hash 2")
        }
    }
    
    @Test
    fun `deleteById should delete RefreshToken`() {
        val user = jpaUserRepository.save(UserEntityFactory.createUserEntity())
        val token = jpaRefreshTokenRepository.save(
            AuthEntityFactory.createRefreshTokenEntity(
                user = user,
                tokenHash = "token hash"
            )
        )
        
        assertTrue(jpaRefreshTokenRepository.existsById(token.id))
        
        refreshTokenRepository.deleteById(token.id)
        
        assertFalse(jpaRefreshTokenRepository.existsById(token.id))
    }
}