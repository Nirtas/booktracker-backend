package ru.jerael.booktracker.backend.factory.publisher

import ru.jerael.booktracker.backend.data.db.entity.PublisherEntity
import java.util.*

object PublisherEntityFactory {
    fun createPublisherEntity(
        id: UUID = UUID.randomUUID(),
        name: String = "Publisher A"
    ): PublisherEntity {
        return PublisherEntity().apply { this.id = id; this.name = name }
    }
}