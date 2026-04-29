package ru.jerael.booktracker.backend.data.external.google.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.data.external.google.dto.IsbnType
import ru.jerael.booktracker.backend.factory.book.BookDataFactory

class GoogleBooksMapperTest {
    private val mapper = GoogleBooksMapper()
    
    @Test
    fun `toBookMetadata should map VolumeInfo to BookMetadata`() {
        val info = BookDataFactory.createVolumeInfo(
            categories = listOf("Fiction"),
            authors = listOf("Author A"),
            publishedDate = "2010-08-15"
        )
        
        val result = mapper.toBookMetadata(info)
        
        with(result) {
            assertThat(title).isEqualTo(info.title)
            assertThat(cover).startsWith("https://")
            assertThat(genres.map { it.name }).contains("Fiction")
            assertThat(authors.map { it.fullName }).contains("Author A")
            assertThat(description).isEqualTo(info.description)
            assertThat(publisher.name).isEqualTo(info.publisher)
            assertThat(language.code).isEqualTo(info.language)
            assertThat(publishedOn).isEqualTo(2010)
            assertThat(totalPages).isEqualTo(info.pageCount)
            assertThat(isbn10).isEqualTo(
                info.industryIdentifiers
                    .filter { it.type == IsbnType.ISBN_10 }
                    .map { it.identifier }
                    .first()
            )
            assertThat(isbn13).isEqualTo(
                info.industryIdentifiers
                    .filter { it.type == IsbnType.ISBN_13 }
                    .map { it.identifier }
                    .first()
            )
        }
    }
    
    @Test
    fun `toBookMetadata should map VolumeInfo without cover`() {
        val info = BookDataFactory.createVolumeInfo(imageLinks = null)
        
        val result = mapper.toBookMetadata(info)
        
        assertThat(result.cover).isNull()
    }
    
    @Test
    fun `toBookMetadata should convert cover url to https`() {
        val info = BookDataFactory.createVolumeInfo(
            imageLinks = BookDataFactory.createImageLinks(
                smallThumbnail = "http://url.com",
                thumbnail = "http://url.com"
            )
        )
        
        val result = mapper.toBookMetadata(info)
        
        assertThat(result.cover).startsWith("https://")
    }
    
    @Test
    fun `toBookMetadata should extract year correctly`() {
        val dates = listOf("2010-08-15", "2010-08", "2010")
        
        dates.forEach { date ->
            val info = BookDataFactory.createVolumeInfo(publishedDate = date)
            
            val result = mapper.toBookMetadata(info)
            
            assertThat(result.publishedOn).isEqualTo(2010)
        }
    }
    
    @Test
    fun `toBookMetadata should extract isbns correctly`() {
        val info = BookDataFactory.createVolumeInfo(
            industryIdentifiers = listOf(
                BookDataFactory.createIndustryIdentifier(type = IsbnType.ISBN_10, identifier = "0765326353")
            )
        )
        
        val result = mapper.toBookMetadata(info)
        
        with(result) {
            assertThat(isbn10).isEqualTo("0765326353")
            assertThat(isbn13).isNull()
        }
    }
}