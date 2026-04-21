package ru.jerael.booktracker.backend.application.usecase.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import ru.jerael.booktracker.backend.application.annotation.UseCase;
import ru.jerael.booktracker.backend.domain.model.auth.LogoutPayload;
import ru.jerael.booktracker.backend.domain.service.token.AuthTokenService;
import ru.jerael.booktracker.backend.domain.usecase.auth.LogoutUserUseCase;
import ru.jerael.booktracker.backend.domain.validator.AuthValidator;

@UseCase
@RequiredArgsConstructor
public class LogoutUserUseCaseImpl implements LogoutUserUseCase {
    private final AuthValidator authValidator;
    private final AuthTokenService authTokenService;

    @Override
    @Transactional
    public void execute(LogoutPayload data) {
        authValidator.validateLogoutPayload(data);
        authTokenService.revokeToken(data.refreshToken());
    }
}
