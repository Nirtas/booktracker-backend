package ru.jerael.booktracker.backend.api.exception.util;

import jakarta.validation.ConstraintViolation;
import lombok.experimental.UtilityClass;
import org.springframework.validation.FieldError;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@UtilityClass
public class ApiErrorUtils {
    private static final Set<String> EXCLUDED_ATTRIBUTES = Set.of("message", "groups", "payload", "flags");

    public static String toSnakeCase(String str) {
        if (str == null) return "UNKNOWN";

        return str.replaceAll("([a-z])([A-Z]+)", "$1_$2").toUpperCase();
    }

    public static Map<String, Object> extractParams(FieldError error) {
        try {
            ConstraintViolation<?> violation = error.unwrap(ConstraintViolation.class);
            Map<String, Object> attrs = violation.getConstraintDescriptor().getAttributes();

            Map<String, Object> params = new HashMap<>();
            attrs.forEach((key, value) -> {
                if (!EXCLUDED_ATTRIBUTES.contains(key)) {
                    params.put(key, value);
                }
            });
            return params;
        } catch (Exception ignored) {
            return Map.of();
        }
    }
}
