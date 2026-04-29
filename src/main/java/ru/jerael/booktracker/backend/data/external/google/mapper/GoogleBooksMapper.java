package ru.jerael.booktracker.backend.data.external.google.mapper;

import ru.jerael.booktracker.backend.data.annotation.DataMapper;
import ru.jerael.booktracker.backend.data.external.google.dto.ImageLinks;
import ru.jerael.booktracker.backend.data.external.google.dto.IndustryIdentifier;
import ru.jerael.booktracker.backend.data.external.google.dto.IsbnType;
import ru.jerael.booktracker.backend.data.external.google.dto.VolumeInfo;
import ru.jerael.booktracker.backend.domain.model.author.Author;
import ru.jerael.booktracker.backend.domain.model.book.BookMetadata;
import ru.jerael.booktracker.backend.domain.model.genre.Genre;
import ru.jerael.booktracker.backend.domain.model.language.Language;
import ru.jerael.booktracker.backend.domain.model.publisher.Publisher;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@DataMapper
public class GoogleBooksMapper {
    public BookMetadata toBookMetadata(VolumeInfo info) {
        if (info == null) return null;

        return new BookMetadata(
            info.title(),
            getCoverUrl(info.imageLinks()),
            mapGenres(info.categories()),
            mapAuthors(info.authors()),
            info.description(),
            mapPublisher(info.publisher()),
            mapLanguage(info.language()),
            extractYear(info.publishedDate()),
            info.pageCount(),
            extractIsbn(info.industryIdentifiers(), IsbnType.ISBN_10),
            extractIsbn(info.industryIdentifiers(), IsbnType.ISBN_13)
        );
    }

    private String getCoverUrl(ImageLinks links) {
        if (links == null) return null;

        String coverUrl = links.thumbnail() != null ? links.thumbnail() : links.smallThumbnail();
        return coverUrl != null ? coverUrl.replace("http://", "https://") : null;
    }

    private Set<Genre> mapGenres(List<String> categories) {
        if (categories == null || categories.isEmpty()) return Set.of();

        return categories.stream()
            .map(name -> new Genre(null, name))
            .collect(Collectors.toSet());
    }

    private Set<Author> mapAuthors(List<String> authors) {
        if (authors == null || authors.isEmpty()) return Set.of();

        return authors.stream()
            .map(name -> new Author(null, name))
            .collect(Collectors.toSet());
    }

    private Publisher mapPublisher(String name) {
        if (name == null || name.isBlank()) return null;

        return new Publisher(null, name);
    }

    private Language mapLanguage(String code) {
        if (code == null || code.isBlank()) return null;

        return new Language(code, null);
    }

    private Integer extractYear(String publishedDate) {
        if (publishedDate == null || publishedDate.isBlank()) return null;

        try {
            return Integer.parseInt(publishedDate.substring(0, 4));
        } catch (Exception e) {
            return null;
        }
    }

    private String extractIsbn(List<IndustryIdentifier> identifiers, IsbnType type) {
        if (identifiers == null || identifiers.isEmpty()) return null;

        return identifiers.stream()
            .filter(identifier -> type.equals(identifier.type()))
            .map(IndustryIdentifier::identifier)
            .findFirst()
            .orElse(null);
    }
}
