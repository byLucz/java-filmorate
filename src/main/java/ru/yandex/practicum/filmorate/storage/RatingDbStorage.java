package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

@Component
public class RatingDbStorage implements RatingStorage {
    private JdbcTemplate jdbcTemplate;

    public RatingDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<MPA> ratingRowMapper = (rs, rowNum) -> {
        int id = rs.getInt("id");
        String ratingCode = rs.getString("rating_code");
        String description = rs.getString("description");
        return new MPA(id, ratingCode, description);
    };

    @Override
    public List<MPA> allRatings() {
        String sql = "SELECT * FROM PUBLIC.RATING";
        return jdbcTemplate.query(sql, ratingRowMapper);
    }

    @Override
    public MPA findRatingById(int id) {
        String sql = "SELECT * FROM PUBLIC.RATING WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, ratingRowMapper);
    }
}
