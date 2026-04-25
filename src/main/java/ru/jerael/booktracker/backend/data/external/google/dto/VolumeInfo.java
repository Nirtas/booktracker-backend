package ru.jerael.booktracker.backend.data.external.google.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record VolumeInfo(
    String title,
    List<String> authors,
    String publisher,
    String publishedDate,
    String description,
    List<IndustryIdentifier> industryIdentifiers,
    Integer pageCount,
    List<String> categories,
    ImageLinks imageLinks,
    String language
) {}
