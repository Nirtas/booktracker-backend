package ru.jerael.booktracker.backend.factory.publisher

import ru.jerael.booktracker.backend.domain.model.publisher.Publisher
import java.util.*

object PublisherDomainFactory {
    fun createPublisher(
        id: UUID = UUID.randomUUID(),
        name: String = "Publisher A"
    ): Publisher {
        return Publisher(id, name)
    }
}