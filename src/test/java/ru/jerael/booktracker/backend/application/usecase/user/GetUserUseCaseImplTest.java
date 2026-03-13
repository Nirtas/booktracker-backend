package ru.jerael.booktracker.backend.application.usecase.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.jerael.booktracker.backend.domain.model.user.User;
import ru.jerael.booktracker.backend.domain.repository.UserRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserUseCaseImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GetUserUseCaseImpl useCase;

    private final UUID userId = UUID.fromString("ee39af7a-a073-4473-878a-1aae34e98bb7");
    private final String email = "test@example.com";
    private final String passwordHash = "password hash";

    @Test
    void execute_WhenFound_ShouldReturnUser() {
        User user = new User(userId, email, passwordHash, true, Instant.now());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = useCase.execute(userId);

        assertEquals(user, result);
        verify(userRepository).findById(userId);
    }
}