package ru.jerael.booktracker.backend.data.external.google.dto;

public record IndustryIdentifier(
    IsbnType type,
    String identifier
) {}
