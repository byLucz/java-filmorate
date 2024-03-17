package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage{
    private Map<Integer, Film> films = new HashMap<>();
    private int filmId = 1;

    @Override
    public List<Film> allFilms() {
        log.info("Выведен список всех фильмов");
        return new ArrayList<>(films.values());
    }

    @Override
    public Film createFilm(Film film) {
        validateFilm(film);
        film.setId(filmId);
        if (film.getLikeCounter() == null)
            film.setLikeCounter(new HashSet<>());
        films.put(film.getId(), film);
        log.info("Добавлен фильм " + film.getName());
        filmId++;
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        validateFilm(film);
        if (!films.containsKey(film.getId())) {
            log.warn("Такого фильма не существует " + film.getName());
            throw new NotFoundException("Фильма не найдено");
        }
        if (film.getLikeCounter() == null)
            film.setLikeCounter(new HashSet<>());
        films.put(film.getId(), film);
        log.info("Обновлен фильм " + film.getName());
        return film;
    }

    @Override
    public void validateFilm(Film film) {
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
