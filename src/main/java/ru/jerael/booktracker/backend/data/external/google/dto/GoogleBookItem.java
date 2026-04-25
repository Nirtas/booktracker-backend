package ru.jerael.booktracker.backend.data.external.google.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleBookItem(
    VolumeInfo volumeInfo
) {}
