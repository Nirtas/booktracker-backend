package ru.jerael.booktracker.backend.data.external.google;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.jerael.booktracker.backend.data.external.google.config.GoogleBooksProperties;
import ru.jerael.booktracker.backend.data.external.google.dto.GoogleBooksResponse;
import ru.jerael.booktracker.backend.data.external.google.dto.VolumeInfo;
import ru.jerael.booktracker.backend.data.external.google.mapper.GoogleBooksMapper;
import ru.jerael.booktracker.backend.domain.gateway.book.BookMetadataProvider;
import ru.jerael.booktracker.backend.domain.model.book.BookMetadata;
import ru.jerael.booktracker.backend.domain.model.book.BookSearchQuery;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoogleBooksMetadataProvider implements BookMetadataProvider {
    private final GoogleBooksClient client;
    private final GoogleBooksProperties properties;
    private final GoogleBooksMapper googleBooksMapper;

    @Override
    public Optional<BookMetadata> findBook(BookSearchQuery query) {
        String q = "isbn:" + query.isbn();
        GoogleBooksResponse response = client.findBook(q, properties.getApiKey());
        if (response == null || response.totalItems() == 0 || response.items() == null) {
            return Optional.empty();
        }
        VolumeInfo info = response.items().get(0).volumeInfo();
        return Optional.of(googleBooksMapper.toBookMetadata(info));
    }
}
