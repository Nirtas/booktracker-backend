package ru.jerael.booktracker.backend.domain.validator;

import ru.jerael.booktracker.backend.domain.model.user.UserCreation;

public interface UserValidator {
    void validateCreation(UserCreation data);
}
