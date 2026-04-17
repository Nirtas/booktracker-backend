package ru.jerael.booktracker.backend

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import ru.jerael.booktracker.backend.config.TestcontainersConfiguration

@SpringBootTest
@Import(TestcontainersConfiguration::class)
class ApplicationTests {
    
    @Test
    fun contextLoads() {
    }
    
}