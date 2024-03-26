package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Qualifier("userDbStorage")
@Slf4j
public class UserDbStorage implements UserStorage {
    private JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());

        Integer friendId = rs.getObject("friend_id", Integer.class);
        if (friendId != null) {
            if (user.getFriends() == null)
                user.setFriends(new HashSet<>());
            user.getFriends().add(friendId);
        }
        return user;
    };

    @Override
    public List<User> allUsers() {
        String sql = "SELECT u.ID AS id, u.name AS name, u.LOGIN AS login, " +
                "u.EMAIL AS mail, u.BIRTHDAY AS birthday, f.USER_ID AS friend_id " +
                "FROM USERS u LEFT JOIN FRIENDSHIP f ON f.FRIEND_ID = u.ID";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    @Override
    public User createUsers(User user) {
        validateUser(user);
        String sql = "INSERT INTO PUBLIC.USERS (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, java.sql.Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        int genUserId = (int) keyHolder.getKey();
        user.setId(genUserId);
        return user;
    }

    @Override
    public User updateUsers(User user) {
        validateUser(user);
        int userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM PUBLIC.USERS", Integer.class);

        if (user.getId() > userCount || user.getId() <= 0) {
            throw new NotFoundException("Пользователя с таким ID не существует");
        }
        String sql = "UPDATE PUBLIC.USERS SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public Map<Integer, User> getUsers() {
        return allUsers().stream().collect(Collectors.toMap(User::getId, user -> user));
    }

    private void validateUser(User user) {
        if (user == null) {
            log.warn("Информации о пользователе не предоставлено");
            throw new ValidationException("Информации о пользователе не предоставлено");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Пустое имя было заменено на логин");
            user.setName(user.getLogin());
        }
    }

}
