package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Qualifier("filmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Film> filmRowMapper = (rs, rowNum) -> {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));

        Integer ratingId = rs.getObject("rating_id", Integer.class);
        if(ratingId != null) {
            MPA mpa = new MPA(ratingId,rs.getString("rating_code"),
                    rs.getString("rating_description"));
            film.setMpa(mpa);
        }

        Integer genreId = rs.getObject("genre_id", Integer.class);
        if (genreId != null) {
            Genre genre = new Genre(genreId,rs.getString("genre_name"));
            film.addGenre(genre);
        }

        Integer likes = rs.getObject("likes", Integer.class);
        if (likes != null) {
            if (film.getLikeCounter() == null)
                film.setLikeCounter(new HashSet<>());
            film.getLikeCounter().add(likes);
        }
        return film;
    };

    @Override
    public List<Film> allFilms() {
        String sql = "SELECT f.ID as film_id, f.NAME as name, f.DESCRIPTION as description, " +
                "f.RELEASE_DATE as release_date, f.DURATION as duration, " + "r.ID as rating_id,"+
                "r.RATING_CODE as rating_code, r.DESCRIPTION as rating_description, " + "g.ID as genre_id," +
                "g.NAME as genre_name, l.USER_ID AS likes " +
                "FROM FILM as f " +
                "LEFT JOIN RATING as r ON r.id = f.RATING_ID " +
                "LEFT JOIN FILMGENRE fg ON fg.FILM_ID = f.ID " +
                "LEFT JOIN GENRE g ON g.ID = fg.GENRE_ID " +
                "LEFT JOIN LIKES l ON l.FILM_ID = f.ID";
        return jdbcTemplate.query(sql, filmRowMapper);
    }
    @Override
    public Film createFilm(Film film) {
        validateFilm(film);
        String sql = "INSERT INTO PUBLIC.FILM (name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, (film.getMpa() != null) ? film.getMpa().getId() : null);
            return ps;
        }, keyHolder);

        int genFilmId = (int) keyHolder.getKey();
        film.setId(genFilmId);
        updateGenre(genFilmId,film.getGenres());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        validateFilm(film);
        String sql = "UPDATE PUBLIC.FILM SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        updateGenre(film.getId(),film.getGenres());
        return film;
    }

    public Map<Integer, Film> getFilms() {
        return allFilms().stream().collect(Collectors.toMap(Film::getId, film -> film));
    }

    private void updateGenre(int id, Set<Genre> gSet) {
        jdbcTemplate.update("DELETE FROM FILMGENRE WHERE FILM_ID = ?", id);
        if (gSet != null && !gSet.isEmpty())
            for (Genre g : gSet) {
                jdbcTemplate.update("INSERT INTO FILMGENRE(FILM_ID,GENRE_ID) VALUES(?,?)", id, g.getId());
            }
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
        for (Genre g : film.getGenres()) {
            if (!isGenreExists(g.getId())) {
                log.warn("Такого жанра с id={} не существует", g.getId());
                throw new ValidationException("Такого жанра с id=" + g.getId() + " не существует");
            }
        }
        if (!isMPAExists(film.getMpa().getId())){
            log.warn("Такого рейтинга не существует");
            throw new ValidationException("Такого рейтинга не существует");
        }
    }
    private boolean isGenreExists(int genreId) {
        String sql = "SELECT COUNT(*) FROM GENRE WHERE ID = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, genreId);
        return count > 0;
    }

    private boolean isMPAExists(int mpaId) {
        String sql = "SELECT COUNT(*) FROM RATING WHERE ID = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, mpaId);
        return count > 0;
    }
}
