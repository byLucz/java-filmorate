package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;

    private final UserStorage userStorage;

    public Film addLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        filmStorage.addLikeToDB(userId, filmId);
        log.info("Пользователь {} успешно поставил лайк фильму {}",user.getName(),film.getName());
        return filmStorage.updateFilm(film);
    }

    public Film deleteLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        if (!film.getLikeCounter().contains(userId))
            throw new NotFoundException("Этот фильм не получал оценок от этого пользователя");
        filmStorage.deleteLikeFromDB(userId, filmId);
        log.info("Пользователь {} успешно удалил лайк фильму {}",user.getName(),film.getName());
        return filmStorage.updateFilm(film);
    }

    public List<Film> getPopular(int count) {
        log.info("Вывод {} самых популярных фильмов", count);
        return filmStorage.findPopularFilms(count);
    }

}
