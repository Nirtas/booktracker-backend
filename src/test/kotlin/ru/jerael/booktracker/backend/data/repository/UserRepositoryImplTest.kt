package ru.jerael.booktracker.backend.data.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.context.annotation.Import
import ru.jerael.booktracker.backend.data.db.repository.JpaUserRepository
import ru.jerael.booktracker.backend.data.mapper.UserDataMapper
import ru.jerael.booktracker.backend.factory.user.UserDomainFactory
import ru.jerael.booktracker.backend.factory.user.UserEntityFactory

@DataJpaTest
@Import(UserRepositoryImpl::class, UserDataMapper::class)
class UserRepositoryImplTest {
    
    @Autowired
    private lateinit var jpaUserRepository: JpaUserRepository
    
    @Autowired
    private lateinit var userRepository: UserRepositoryImpl
    
    @Test
    fun `when user exists, findById should return user`() {
        val user = jpaUserRepository.save(UserEntityFactory.createUserEntity())
        
        val result = userRepository.findById(user.id)
        
        with(result.get()) {
            assertEquals(user.id, id)
            assertEquals(user.email, email)
        }
    }
    
    @Test
    fun `when user exists, findByEmail should return user`() {
        val user = jpaUserRepository.save(UserEntityFactory.createUserEntity())
        
        val result = userRepository.findByEmail(user.email)
        
        with(result.get()) {
            assertEquals(user.id, id)
            assertEquals(user.email, email)
        }
    }
    
    @Test
    fun `when id is null, save should insert new user`() {
        val user = UserDomainFactory.createUser(id = null)
        
        val result = userRepository.save(user)
        
        with(result) {
            assertTrue(jpaUserRepository.existsById(id))
            assertEquals(user.email, email)
            assertEquals(user.passwordHash, passwordHash)
            assertEquals(user.isVerified, isVerified)
            assertEquals(user.createdAt, createdAt)
        }
    }
    
    @Test
    fun `when id is present, save should update existing user`() {
        val user = jpaUserRepository.save(UserEntityFactory.createUserEntity(isVerified = false))
        
        val updatedUser = UserDomainFactory.createUser(id = user.id, isVerified = true)
        
        val result = userRepository.save(updatedUser)
        
        with(result) {
            assertEquals(user.id, id)
            assertEquals(updatedUser.isVerified, isVerified)
        }
    }
}