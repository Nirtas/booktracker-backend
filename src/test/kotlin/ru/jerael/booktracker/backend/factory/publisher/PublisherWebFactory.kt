package ru.jerael.booktracker.backend.factory.publisher

import ru.jerael.booktracker.backend.web.dto.publisher.PublisherResponse
import java.util.*

object PublisherWebFactory {
    fun createPublisherResponse(
        id: UUID? = UUID.randomUUID(),
        name: String = "Publisher A"
    ): PublisherResponse {
        return PublisherResponse(id, name)
    }
}