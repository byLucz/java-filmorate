package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface FilmStorage {
    List<Film> allFilms();

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Map<Integer, Film> getFilms();

}
