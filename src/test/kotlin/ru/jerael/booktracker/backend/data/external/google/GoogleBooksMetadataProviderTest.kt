package ru.jerael.booktracker.backend.data.external.google

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.ninjasquad.springmockk.MockkBean
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.wiremock.spring.ConfigureWireMock
import org.wiremock.spring.EnableWireMock
import ru.jerael.booktracker.backend.data.exception.code.ExternalApiErrorCode
import ru.jerael.booktracker.backend.domain.exception.InternalException
import ru.jerael.booktracker.backend.domain.storage.BookCoverStorage
import ru.jerael.booktracker.backend.factory.book.BookDomainFactory

@SpringBootTest(properties = ["app.external.google-books.api-key=test-api-key"])
@EnableWireMock(ConfigureWireMock(baseUrlProperties = ["app.external.google-books.base-url"]))
@ActiveProfiles("test")
class GoogleBooksMetadataProviderTest {
    
    @MockkBean
    private lateinit var bookCoverStorage: BookCoverStorage
    
    @Autowired
    private lateinit var provider: GoogleBooksMetadataProvider
    
    @Test
    fun `when book found, findBook should return BookMetadata`() {
        val isbn = "9780765326355"
        val query = BookDomainFactory.createBookSearchQuery(isbn = isbn)
        
        stubFor(
            get(urlPathEqualTo("/volumes"))
                .withQueryParam("q", equalTo("isbn:$isbn"))
                .withQueryParam("key", equalTo("test-api-key"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("google-books.json")
                )
        )
        
        val result = provider.findBook(query)
        
        with(result.get()) {
            assertThat(title).isEqualTo("The Way of Kings")
            assertThat(cover).startsWith("https://")
            assertThat(description).startsWith("The conclusion to")
            assertThat(publisher.name).isEqualTo("Tor Books")
            assertThat(language.code).isEqualTo("en")
            assertThat(totalPages).isEqualTo(1008)
        }
    }
    
    @Test
    fun `when api returns error, findBook should throw InternalException`() {
        val query = BookDomainFactory.createBookSearchQuery()
        
        stubFor(
            get(urlPathEqualTo("/volumes"))
                .willReturn(
                    aResponse()
                        .withStatus(500)
                        .withBody("Internal Server Error")
                )
        )
        
        val exception = assertThrows(InternalException::class.java) {
            provider.findBook(query)
        }
        
        assertThat(exception.message).contains("API error")
        assertThat(exception.errorCode).isEqualTo(ExternalApiErrorCode.ERROR)
    }
}