package ru.jerael.booktracker.backend.web.exception.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.metadata.ConstraintDescriptor;
import org.junit.jupiter.api.Test;
import org.springframework.validation.FieldError;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class WebErrorUtilsTest {

    @Test
    void toSnakeCase() {
        assertEquals("NOT_BLANK", WebErrorUtils.toSnakeCase("NotBlank"));
        assertEquals("SIZE", WebErrorUtils.toSnakeCase("Size"));
        assertEquals("UNKNOWN", WebErrorUtils.toSnakeCase(null));
    }

    @Test
    void extractParams() {
        FieldError fieldError = mock(FieldError.class);
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        ConstraintDescriptor<?> descriptor = mock(ConstraintDescriptor.class);
        Map<String, Object> attrs = Map.of(
            "min", 0,
            "max", 500,
            "message", "{jakarta.validation.constraints.Size.message}",
            "groups", new Object[]{},
            "payload", new Object[]{}
        );
        doReturn(violation).when(fieldError).unwrap(ConstraintViolation.class);
        doReturn(descriptor).when(violation).getConstraintDescriptor();
        doReturn(attrs).when(descriptor).getAttributes();

        Map<String, Object> params = WebErrorUtils.extractParams(fieldError);

        assertThat(params)
            .hasSize(2)
            .containsEntry("min", 0)
            .containsEntry("max", 500)
            .doesNotContainKeys("message", "groups", "payload");
    }
}