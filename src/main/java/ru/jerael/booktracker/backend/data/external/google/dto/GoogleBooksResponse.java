package ru.jerael.booktracker.backend.data.external.google.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleBooksResponse(
    Integer totalItems,
    List<GoogleBookItem> items
) {}
