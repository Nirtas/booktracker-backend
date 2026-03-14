package ru.jerael.booktracker.backend.data.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.jerael.booktracker.backend.data.db.entity.UserEntity;
import ru.jerael.booktracker.backend.data.db.repository.JpaUserRepository;
import ru.jerael.booktracker.backend.data.mapper.UserDataMapper;
import ru.jerael.booktracker.backend.domain.model.user.User;
import ru.jerael.booktracker.backend.domain.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final JpaUserRepository jpaUserRepository;
    private final UserDataMapper userDataMapper;

    @Override
    public Optional<User> findById(UUID id) {
        return jpaUserRepository.findById(id).map(userDataMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email).map(userDataMapper::toDomain);
    }

    @Override
    public User save(User user) {
        UserEntity entity = userDataMapper.toEntity(user);
        UserEntity savedEntity = jpaUserRepository.save(entity);
        return userDataMapper.toDomain(savedEntity);
    }
}
