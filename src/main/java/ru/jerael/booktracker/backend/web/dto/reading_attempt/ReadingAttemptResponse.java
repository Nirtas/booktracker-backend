package ru.jerael.booktracker.backend.web.dto.reading_attempt;

import jakarta.annotation.Nullable;
import ru.jerael.booktracker.backend.web.dto.reading_session.ReadingSessionResponse;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ReadingAttemptResponse(
    UUID id,
    String status,
    Instant startedAt,
    @Nullable Instant finishedAt,
    List<ReadingSessionResponse> sessions
) {}
