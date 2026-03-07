package ru.jerael.booktracker.backend.domain.exception.model;

import java.util.Map;

public record ValidationError(
    String code,
    String field,
    String message,
    Map<String, Object> params
) {}
