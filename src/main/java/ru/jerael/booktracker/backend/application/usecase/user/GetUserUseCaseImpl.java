package ru.jerael.booktracker.backend.application.usecase.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jerael.booktracker.backend.domain.exception.factory.UserExceptionFactory;
import ru.jerael.booktracker.backend.domain.model.user.User;
import ru.jerael.booktracker.backend.domain.repository.UserRepository;
import ru.jerael.booktracker.backend.domain.usecase.user.GetUserUseCase;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetUserUseCaseImpl implements GetUserUseCase {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public User execute(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> UserExceptionFactory.userNotFound(userId));
    }
}
