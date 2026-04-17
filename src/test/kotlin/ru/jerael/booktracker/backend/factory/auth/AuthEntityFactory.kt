package ru.jerael.booktracker.backend.factory.auth

import ru.jerael.booktracker.backend.data.db.entity.RefreshTokenEntity
import ru.jerael.booktracker.backend.data.db.entity.UserEntity
import java.time.Instant
import java.util.*

object AuthEntityFactory {
    fun createRefreshTokenEntity(
        id: UUID? = UUID.randomUUID(),
        user: UserEntity? = UserEntity().apply { this.id = UUID.randomUUID() },
        tokenHash: String = "token hash",
        expiresAt: Instant = Instant.now().plusSeconds(6000)
    ): RefreshTokenEntity {
        return RefreshTokenEntity().apply {
            this.id = id; this.user = user; this.tokenHash = tokenHash; this.expiresAt = expiresAt
        }
    }
}
