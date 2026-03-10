package ru.jerael.booktracker.backend.domain.validator;

import ru.jerael.booktracker.backend.domain.exception.model.ValidationError;
import java.util.List;

public interface FieldValidator {
    List<ValidationError> validateEmail(String email);

    List<ValidationError> validatePassword(String password);
}
