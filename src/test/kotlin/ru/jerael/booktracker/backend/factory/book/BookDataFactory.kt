package ru.jerael.booktracker.backend.factory.book

import ru.jerael.booktracker.backend.data.external.google.dto.ImageLinks
import ru.jerael.booktracker.backend.data.external.google.dto.IndustryIdentifier
import ru.jerael.booktracker.backend.data.external.google.dto.IsbnType
import ru.jerael.booktracker.backend.data.external.google.dto.VolumeInfo

object BookDataFactory {
    fun createVolumeInfo(
        title: String? = "title",
        authors: List<String>? = listOf("Author A"),
        publisher: String? = "Publisher",
        publishedDate: String? = "2010-08-31",
        description: String? = "description",
        industryIdentifiers: List<IndustryIdentifier>? = listOf(
            IndustryIdentifier(IsbnType.ISBN_10, "0765326353"),
            IndustryIdentifier(IsbnType.ISBN_13, "9780765326355")
        ),
        pageCount: Int? = 1008,
        categories: List<String>? = listOf("Fiction"),
        imageLinks: ImageLinks? = ImageLinks(
            "http://url.com",
            "http://url.com"
        ),
        language: String? = "en"
    ): VolumeInfo {
        return VolumeInfo(
            title, authors, publisher, publishedDate, description, industryIdentifiers, pageCount,
            categories, imageLinks, language
        )
    }
    
    fun createIndustryIdentifier(
        type: IsbnType? = IsbnType.ISBN_10,
        identifier: String? = "0765326353"
    ): IndustryIdentifier {
        return IndustryIdentifier(type, identifier)
    }
    
    fun createImageLinks(
        smallThumbnail: String? = "http://url.com",
        thumbnail: String? = "http://url.com"
    ): ImageLinks {
        return ImageLinks(smallThumbnail, thumbnail)
    }
}