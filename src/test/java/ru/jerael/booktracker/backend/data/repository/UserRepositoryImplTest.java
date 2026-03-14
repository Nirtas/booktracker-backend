package ru.jerael.booktracker.backend.data.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.jerael.booktracker.backend.data.db.entity.UserEntity;
import ru.jerael.booktracker.backend.data.db.repository.JpaUserRepository;
import ru.jerael.booktracker.backend.data.mapper.UserDataMapper;
import ru.jerael.booktracker.backend.domain.model.user.User;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({UserRepositoryImpl.class, UserDataMapper.class})
class UserRepositoryImplTest {

    @Autowired
    private UserRepositoryImpl userRepository;

    @Autowired
    private JpaUserRepository jpaUserRepository;

    private final String email = "test@example.com";
    private final String passwordHash = "password hash";
    private final boolean isVerified = true;
    private final Instant createdAt = Instant.ofEpochMilli(1771249699347L);

    @Test
    void findById_WhenExists_ShouldReturnUser() {
        UserEntity entity = new UserEntity();
        entity.setEmail(email);
        entity.setPasswordHash(passwordHash);
        entity.setVerified(isVerified);
        entity.setCreatedAt(createdAt);
        UserEntity savedUser = jpaUserRepository.save(entity);
        UUID userId = savedUser.getId();

        Optional<User> result = userRepository.findById(userId);

        assertTrue(result.isPresent());
        assertEquals(userId, result.get().id());
        assertEquals(email, result.get().email());
    }

    @Test
    void findByEmail_WhenExists_ShouldReturnUser() {
        UserEntity entity = new UserEntity();
        entity.setEmail(email);
        entity.setPasswordHash(passwordHash);
        entity.setVerified(isVerified);
        entity.setCreatedAt(createdAt);

        UserEntity savedUser = jpaUserRepository.save(entity);
        UUID userId = savedUser.getId();

        Optional<User> result = userRepository.findByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(userId, result.get().id());
        assertEquals(email, result.get().email());
    }

    @Test
    void save_WhenIdIsNull_ShouldInsertNewUser() {
        User user = new User(null, email, passwordHash, isVerified, createdAt);

        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.id());
        assertEquals(email, savedUser.email());
        assertEquals(passwordHash, savedUser.passwordHash());
        assertEquals(isVerified, savedUser.isVerified());
        assertEquals(createdAt, savedUser.createdAt());
        assertTrue(jpaUserRepository.existsById(savedUser.id()));
    }

    @Test
    void save_WhenIdIsPresent_ShouldUpdateExistingUser() {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        userEntity.setPasswordHash(passwordHash);
        userEntity.setVerified(false);
        userEntity.setCreatedAt(createdAt);

        UserEntity savedUser = jpaUserRepository.save(userEntity);
        UUID userId = savedUser.getId();

        User user = new User(
            userId,
            "new email",
            "new password hash",
            true,
            savedUser.getCreatedAt()
        );

        User updatedUser = userRepository.save(user);

        assertEquals(userId, updatedUser.id());
        assertEquals("new email", updatedUser.email());
        assertEquals("new password hash", updatedUser.passwordHash());
        assertTrue(updatedUser.isVerified());
        assertEquals(createdAt, updatedUser.createdAt());
    }
}