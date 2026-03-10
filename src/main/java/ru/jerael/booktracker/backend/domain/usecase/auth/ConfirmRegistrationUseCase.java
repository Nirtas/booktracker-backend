package ru.jerael.booktracker.backend.domain.usecase.auth;

import ru.jerael.booktracker.backend.domain.model.auth.ConfirmRegistration;
import ru.jerael.booktracker.backend.domain.model.auth.TokenPair;

public interface ConfirmRegistrationUseCase {
    TokenPair execute(ConfirmRegistration data);
}
