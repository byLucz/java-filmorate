package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private Map<Integer, Film> films = new HashMap<>();
    private int filmId = 1;

    @GetMapping
    public List<Film> allFilms() {
        log.info("Выведен список всех фильмов");
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        validateFilm(film);
        film.setId(filmId);
        films.put(film.getId(), film);
        log.info("Добавлен фильм " + film.getName());
        filmId++;
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        validateFilm(film);
        if (!films.containsKey(film.getId())) {
            log.warn("Такого фильма не существует " + film.getName());
            throw new NotFoundException("Фильма не найдено");
        }
        films.put(film.getId(), film);
        log.info("Обновлен фильм " + film.getName());
        return film;
    }

    private void validateFilm(Film film) {
        if (film == null) {
            log.warn("Информации о фильме не предоставлено");
            throw new ValidationException("Информации о фильме не предоставлено");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Описание содержит более 200 символов");
            throw new ValidationException("Описание не может содержать больше 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Фильм не может быть снят раньше 28 декабря 1895 года");
            throw new ValidationException("Фильм не может быть снят раньше 28 декабря 1895 года");
        }
    }
}