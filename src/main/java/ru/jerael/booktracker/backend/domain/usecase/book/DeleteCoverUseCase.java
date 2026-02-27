package ru.jerael.booktracker.backend.domain.usecase.book;

import java.util.UUID;

public interface DeleteCoverUseCase {
    void execute(UUID bookId);
}
