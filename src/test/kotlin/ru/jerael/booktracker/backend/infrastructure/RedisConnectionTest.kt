package ru.jerael.booktracker.backend.infrastructure

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.StringRedisTemplate
import ru.jerael.booktracker.backend.config.TestcontainersConfiguration

@SpringBootTest
@Import(TestcontainersConfiguration::class)
class RedisConnectionTest {
    
    @Autowired
    private lateinit var redisTemplate: StringRedisTemplate
    
    @Test
    fun `should connect to redis`() {
        val result = redisTemplate.connectionFactory?.connection?.ping()
        
        assertEquals("PONG", result)
        
        val ops = redisTemplate.opsForValue()
        val key = "key"
        val value = "value"
        
        ops.set(key, value)
        
        assertEquals(value, ops.get(key))
    }
}