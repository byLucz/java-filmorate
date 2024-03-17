package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;

public interface FilmStorage {
    List<Film> allFilms();

    Film createFilm(Film film);

    Film updateFilm(Film film);

    void validateFilm(Film film);
}