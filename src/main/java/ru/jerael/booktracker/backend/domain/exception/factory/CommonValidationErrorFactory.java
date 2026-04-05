package ru.jerael.booktracker.backend.domain.exception.factory;

import ru.jerael.booktracker.backend.domain.exception.code.CommonValidationErrorCode;
import ru.jerael.booktracker.backend.domain.exception.model.ValidationError;
import java.util.Map;
import java.util.Set;

public class CommonValidationErrorFactory {
    public static ValidationError emptyField(String field) {
        return new ValidationError(
            CommonValidationErrorCode.FIELD_CANNOT_BE_EMPTY.name(),
            field,
            "Field cannot be empty",
            Map.of()
        );
    }

    public static ValidationError fieldTooLong(String field, int max) {
        return new ValidationError(
            CommonValidationErrorCode.FIELD_TOO_LONG.name(),
            field,
            "Field too long",
            Map.of("max", max)
        );
    }

    public static ValidationError fieldTooShort(String field, int min) {
        return new ValidationError(
            CommonValidationErrorCode.FIELD_TOO_SHORT.name(),
            field,
            "Field too short",
            Map.of("min", min)
        );
    }

    public static ValidationError invalidLength(String field, int length) {
        return new ValidationError(
            CommonValidationErrorCode.INVALID_LENGTH.name(),
            field,
            "Field must have exactly " + length + " characters",
            Map.of("length", length)
        );
    }

    public static ValidationError emptyListItem(String field) {
        return new ValidationError(
            CommonValidationErrorCode.FIELD_CANNOT_BE_EMPTY.name(),
            field,
            "List items cannot be empty",
            Map.of()
        );
    }

    public static ValidationError listItemsTooLong(String field, Set<?> items, int max) {
        return new ValidationError(
            CommonValidationErrorCode.FIELD_TOO_LONG.name(),
            field,
            "List item too long",
            Map.of("items", items, "max", max)
        );
    }
}
