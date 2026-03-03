package ru.jerael.booktracker.backend.web.exception.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

public record FieldErrorDetail(
    String field,
    String code,
    String message,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    Map<String, Object> params
) {}
