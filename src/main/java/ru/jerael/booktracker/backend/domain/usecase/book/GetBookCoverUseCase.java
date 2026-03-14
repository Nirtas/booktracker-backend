package ru.jerael.booktracker.backend.domain.usecase.book;

import ru.jerael.booktracker.backend.domain.model.image.ImageFile;
import java.util.UUID;

public interface GetBookCoverUseCase {
    ImageFile execute(UUID id, UUID userId);
}
