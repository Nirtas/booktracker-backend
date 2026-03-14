package ru.jerael.booktracker.backend.data.mapper;

import org.springframework.stereotype.Component;
import ru.jerael.booktracker.backend.data.db.entity.UserEntity;
import ru.jerael.booktracker.backend.domain.model.user.User;

@Component
public class UserDataMapper {
    public User toDomain(UserEntity entity) {
        if (entity == null) return null;

        return new User(
            entity.getId(),
            entity.getEmail(),
            entity.getPasswordHash(),
            entity.isVerified(),
            entity.getCreatedAt()
        );
    }

    public UserEntity toEntity(User user) {
        if (user == null) return null;

        UserEntity entity = new UserEntity();
        entity.setId(user.id());
        entity.setEmail(user.email());
        entity.setPasswordHash(user.passwordHash());
        entity.setVerified(user.isVerified());
        entity.setCreatedAt(user.createdAt());
        return entity;
    }
}
