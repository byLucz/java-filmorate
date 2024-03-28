package ru.yandex.practicum.filmorate.storage;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Component
public class GenreDbStorage implements GenreStorage{
    private JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Genre> genreRowMapper = (rs, rowNum) -> {
        int id = rs.getInt("id");
        String name = rs.getString("name");

        return new Genre(id, name);
    };
    @Override
    public List<Genre> allGenres() {
        String sql = "SELECT * FROM PUBLIC.GENRE";
        return jdbcTemplate.query(sql, genreRowMapper);
    }

    @Override
    public Genre findByGenreId(int id) {
        String sql = "SELECT * FROM PUBLIC.GENRE WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, genreRowMapper);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Такого жанра нет");
        }
    }
}
