package ru.jerael.booktracker.backend.web.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.jerael.booktracker.backend.domain.model.reading_attempt.ReadingAttempt;
import ru.jerael.booktracker.backend.web.dto.reading_attempt.ReadingAttemptResponse;

@Component
@RequiredArgsConstructor
public class ReadingAttemptWebMapper {
    private final ReadingSessionWebMapper readingSessionWebMapper;

    public ReadingAttemptResponse toResponse(ReadingAttempt readingAttempt) {
        if (readingAttempt == null) return null;

        return new ReadingAttemptResponse(
            readingAttempt.id(),
            readingAttempt.status().getValue(),
            readingAttempt.startedAt(),
            readingAttempt.finishedAt(),
            readingAttempt.sessions().stream().map(readingSessionWebMapper::toResponse).toList()
        );
    }
}
