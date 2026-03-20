package ru.jerael.booktracker.backend.data.mapper;

import org.springframework.stereotype.Component;
import ru.jerael.booktracker.backend.data.db.entity.EmailVerificationEntity;
import ru.jerael.booktracker.backend.data.db.entity.UserEntity;
import ru.jerael.booktracker.backend.domain.model.verification.EmailVerification;

@Component
public class EmailVerificationDataMapper {
    public EmailVerification toDomain(EmailVerificationEntity entity) {
        if (entity == null) return null;

        return new EmailVerification(
            entity.getId(),
            entity.getUser().getId(),
            entity.getEmail(),
            entity.getType(),
            entity.getToken(),
            entity.getExpiresAt(),
            entity.getCreatedAt()
        );
    }

    public EmailVerificationEntity toEntity(EmailVerification emailVerification) {
        if (emailVerification == null) return null;

        EmailVerificationEntity entity = new EmailVerificationEntity();
        entity.setId(emailVerification.id());

        UserEntity userEntity = new UserEntity();
        userEntity.setId(emailVerification.userId());
        entity.setUser(userEntity);

        entity.setEmail(emailVerification.email());
        entity.setType(emailVerification.type());
        entity.setToken(emailVerification.token());
        entity.setExpiresAt(emailVerification.expiresAt());
        entity.setCreatedAt(emailVerification.createdAt());
        return entity;
    }
}
