package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Film> filmRowMapper = (rs, rowNum) -> {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int duration = rs.getInt("duration");
        Integer ratingId = (Integer) rs.getObject("rating_id");

        return new Film(id, name, description, releaseDate, duration, ratingId);
    };

    @Override
    public List<Film> allFilms() {
        String sql = "SELECT * FROM PUBLIC.FILM";
        return jdbcTemplate.query(sql, filmRowMapper);
    }

    @Override
    public Film createFilm(Film film) {
        String sql = "INSERT INTO PUBLIC.FILM (name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getRatingID());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE PUBLIC.FILM SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getRatingID(), film.getId());
        return film;
    }

    public Map<Integer, Film> getFilms() {
        return allFilms().stream().collect(Collectors.toMap(Film::getId, film -> film));
    }

    public Film findById(int id) {
        String sql = "SELECT * FROM PUBLIC.FILM WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, filmRowMapper);
    }
}
