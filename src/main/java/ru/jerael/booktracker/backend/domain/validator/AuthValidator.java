package ru.jerael.booktracker.backend.domain.validator;

import ru.jerael.booktracker.backend.domain.model.auth.ConfirmRegistration;

public interface AuthValidator {
    void validateRegistrationConfirmation(ConfirmRegistration data);
}
