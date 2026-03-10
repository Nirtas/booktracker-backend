package ru.jerael.booktracker.backend.application.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.jerael.booktracker.backend.domain.exception.ValidationException;
import ru.jerael.booktracker.backend.domain.exception.model.ValidationError;
import ru.jerael.booktracker.backend.domain.model.user.UserCreation;
import ru.jerael.booktracker.backend.domain.validator.FieldValidator;
import ru.jerael.booktracker.backend.domain.validator.UserValidator;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserValidatorImpl implements UserValidator {
    private final FieldValidator fieldValidator;

    @Override
    public void validateCreation(UserCreation data) {
        List<ValidationError> errors = new ArrayList<>();
        errors.addAll(fieldValidator.validateEmail(data.email()));
        errors.addAll(fieldValidator.validatePassword(data.password()));
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
