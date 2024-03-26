package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private final JdbcTemplate jdbcTemplate;
    private final FilmStorage filmStorage;

    public FilmService(JdbcTemplate jdbcTemplate, @Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmStorage = filmStorage;
    }

    public Film addLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilms().get(filmId);
        String sql = "INSERT INTO LIKES(user_id, film_id) VALUES (?,?)";
        jdbcTemplate.update(sql, userId, filmId);
        log.info("Пользователь c id {} успешно поставил лайк фильму {}",userId,film.getName());
        return filmStorage.updateFilm(film);
    }

    public Film deleteLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilms().get(filmId);
        if (!film.getLikeCounter().contains(userId))
            throw new NotFoundException("Этот фильм не получал оценок от этого пользователя");
        String sql = "DELETE FROM LIKES l WHERE l.USER_ID=" + userId + " and l.FILM_ID =" + filmId;
        jdbcTemplate.execute(sql);
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
