package ru.jerael.booktracker.backend.data.verification

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.data.verification.config.OtpCodeProperties
import java.time.Duration

class OtpCodeGeneratorTest {
    private val length = 6
    private val expiry = Duration.ofMinutes(10)
    
    private val generator = OtpCodeGenerator(
        OtpCodeProperties().apply {
            length = length
            expiry = expiry
        }
    )
    
    @Test
    fun `generate should return code with correct length and expiry`() {
        val token = generator.generate()
        
        assertEquals(length, token.value.length)
        assertEquals(expiry, token.expiry)
    }
    
    @Test
    fun `generate should contains only digits`() {
        val token = generator.generate()
        
        assertThat(token.value).containsOnlyDigits()
    }
    
    @Test
    fun `generate should return different codes`() {
        val token1 = generator.generate()
        val token2 = generator.generate()
        
        assertNotEquals(token1, token2)
    }
}