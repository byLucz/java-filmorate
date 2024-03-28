package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> allFilms();

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Film getFilmById(int id);

    List<Film> findPopularFilms(int count);

    void addLikeToDB(Integer userId, Integer filmId);

    void deleteLikeFromDB(Integer userId, Integer filmId);

}
