package ru.jerael.booktracker.backend.web.exception.util

import io.mockk.every
import io.mockk.mockk
import jakarta.validation.ConstraintViolation
import jakarta.validation.metadata.ConstraintDescriptor
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.validation.FieldError

class WebErrorUtilsTest {
    
    @Test
    fun `toSnakeCase should convert CamelCase to SNAKE_CASE`() {
        assertEquals("NOT_BLANK", WebErrorUtils.toSnakeCase("NotBlank"))
        assertEquals("SIZE", WebErrorUtils.toSnakeCase("Size"))
        assertEquals("UNKNOWN", WebErrorUtils.toSnakeCase(null))
    }
    
    @Test
    fun `extractParams should return only validation parameters`() {
        val fieldError = mockk<FieldError>()
        val violation = mockk<ConstraintViolation<*>>()
        val descriptor = mockk<ConstraintDescriptor<*>>()
        val attrs = mapOf(
            "min" to 0,
            "max" to 500,
            "message" to "{jakarta.validation.constraints.Size.message}",
            "groups" to emptyArray<Any>(),
            "payload" to emptyArray<Any>()
        )
        every { fieldError.unwrap(ConstraintViolation::class.java) } returns violation
        every { violation.constraintDescriptor } returns descriptor
        every { descriptor.attributes } returns attrs
        
        val params = WebErrorUtils.extractParams(fieldError)
        
        assertThat(params)
            .hasSize(2)
            .containsEntry("min", 0)
            .containsEntry("max", 500)
            .doesNotContainKeys("message", "groups", "payload", "flags")
    }
}