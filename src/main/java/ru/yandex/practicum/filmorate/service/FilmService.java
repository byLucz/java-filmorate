package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addLike(Integer filmId, Integer userId) {
        Film film = filmStorage.allFilms().get(--filmId);
        film.getLikeCounter().add(userId);
        log.info("Пользователь c id {} успешно поставил лайк фильму {}",userId,film.getName());
        return filmStorage.updateFilm(film);
    }

    public Film deleteLike(Integer filmId, Integer userId) {
        Film film = filmStorage.allFilms().get(--filmId);
        if (!film.getLikeCounter().contains(userId))
            throw new NotFoundException("Этот фильм не получал оценок от этого пользователя");
        film.getLikeCounter().remove(userId);
        log.info("Пользователь c id {} успешно удалил лайк фильму {}",userId,film.getName());
        return filmStorage.updateFilm(film);
    }

    public List<Film> getPopular(int count) {
        log.info("Вывод {} самых популярных фильмов", count);
        return filmStorage.allFilms().stream()
                .sorted(Comparator.comparingInt(film -> -1 * film.getLikeCounter().size()))
                .limit(count)
                .collect(Collectors.toList());
    }


}
