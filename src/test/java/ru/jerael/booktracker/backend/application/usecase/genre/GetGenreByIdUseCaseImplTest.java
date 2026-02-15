package ru.jerael.booktracker.backend.application.usecase.genre;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.jerael.booktracker.backend.domain.exception.NotFoundException;
import ru.jerael.booktracker.backend.domain.model.Genre;
import ru.jerael.booktracker.backend.domain.repository.GenreRepository;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetGenreByIdUseCaseImplTest {

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private GetGenreByIdUseCaseImpl useCase;

    @Test
    void execute_WhenGenreExists_ShouldReturnGenre() {
        Integer genreId = 1;
        Genre genre = new Genre(genreId, "adventure");
        when(genreRepository.getGenreById(genreId)).thenReturn(Optional.of(genre));

        Genre result = useCase.execute(genreId);

        assertEquals(genre, result);
        verify(genreRepository).getGenreById(genreId);
    }

    @Test
    void execute_WhenGenreDoesNotExists_ShouldThrowNotFoundException() {
        Integer genreId = 5555;
        when(genreRepository.getGenreById(genreId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> useCase.execute(genreId));
    }
}