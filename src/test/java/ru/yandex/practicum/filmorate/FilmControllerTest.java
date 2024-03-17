package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FilmControllerTest {
    private InMemoryFilmStorage filmStorage;
    private FilmService filmService;
    private FilmController filmController;
    private Film film;

    @BeforeEach
    void prepareData() {
        filmController = new FilmController(filmStorage, filmService);

        film = Film.builder()
                .name("Титаник")
                .description("Ура фильм о любви")
                .duration(194)
                .releaseDate(LocalDate.of(1998, 2, 28))
                .build();
    }

    @Test
    void shouldThrowInvalidDate() {
        film.setReleaseDate(LocalDate.of(1700, 1, 1));

        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.createFilm(film);
        });

        assertTrue(exception.getMessage().contains("Фильм не может быть снят раньше 28 декабря 1895 года"));
    }

    @Test
    void shouldAddCorrectDataFilm() {
        filmController.createFilm(film);
        List<Film> films = filmController.allFilms();

        assertEquals(1,films.size());
        assertEquals(film, films.get(0));
    }

    @Test
    void shouldThrowInvalidDesc() {
        film.setDescription("L".repeat(201));

        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.createFilm(film);
        });

        assertTrue(exception.getMessage().contains("Описание не может содержать больше 200 символов"));
    }
}