package ru.jerael.booktracker.backend.data.hasher

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import ru.jerael.booktracker.backend.data.hasher.config.Argon2Properties

class Argon2PasswordHasherTest {
    private val hasher = Argon2PasswordHasher(
        Argon2Properties().apply {
            saltLength = 4
            hashLength = 8
            parallelism = 1
            memory = 16
            iterations = 1
        }
    )
    
    @Test
    fun `hash should return valid argon2 hash string`() {
        val password = "password"
        
        val hash = hasher.hash(password)
        
        assertNotNull(hash)
        assertThat(hash).startsWith($$"$argon2id$")
    }
    
    @Test
    fun `hash should produce different hashes for same password`() {
        val password = "password"
        
        val hash1 = hasher.hash(password)
        val hash2 = hasher.hash(password)
        
        assertNotEquals(hash1, hash2)
        assertTrue {
            hasher.verify(password, hash1)
            hasher.verify(password, hash2)
        }
    }
    
    @Test
    fun `verify should return true for correct password`() {
        val password = "password"
        val hash = hasher.hash(password)
        
        val isValid = hasher.verify(password, hash)
        
        assertTrue(isValid)
    }
    
    @Test
    fun `verify should return false for incorrect password`() {
        val password = "password"
        val hash = hasher.hash(password)
        
        val isValid = hasher.verify("wrong password", hash)
        
        assertFalse(isValid)
    }
}