package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class UserDbStorage implements UserStorage{
    private final JdbcTemplate jdbcTemplate;

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
            Set<Integer> friends = user.getFriends();

            if (friends == null) {
                friends = new LinkedHashSet<>();
                user.setFriends(friends);
            }
            friends.add(friendId);
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

        return getUserById(genUserId);
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
        return getUserById(user.getId());
    }

    @Override
    public User getUserById(int id) {
        String sql = "SELECT u.ID AS id, u.name AS name, u.LOGIN AS login, " +
                "u.EMAIL AS mail, u.BIRTHDAY AS birthday, f.FRIEND_ID AS friend_id " +
                "FROM USERS u LEFT JOIN FRIENDSHIP f ON f.FRIEND_ID = u.ID " +
                "WHERE u.ID = ?";

        List<User> users = jdbcTemplate.query(sql, userRowMapper, id);
        if (users.isEmpty())
            throw new NotFoundException("Пользователь с ID " + id + " не найден");
        return users.get(0);
    }

    @Override
    public void addFriend(int userId, int friendId) {
        deleteFriend(userId, friendId);
        if (!isUserExists(userId) || !isUserExists(friendId)) {
            throw new NotFoundException("Такого пользователя или друга не существует");
        }
        if (areFriends(userId, friendId)) {
            throw new ValidationException("Пользователи уже являются друзьями");
        }
        String sql = "INSERT INTO FRIENDSHIP (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
        updateFriendshipStatus(userId,friendId);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        if (!isUserExists(userId) || !isUserExists(friendId)) {
            throw new NotFoundException("Такого пользователя или друга не существует");
        }
        String deleteSql = "DELETE FROM FRIENDSHIP WHERE USER_ID=" + userId + " and FRIEND_ID=" + friendId;
        jdbcTemplate.execute(deleteSql);
        updateFriendshipStatus(userId,friendId);
    }

    @Override
    public List<User> getFriendsList(int userId) {
        String sql = "SELECT f.FRIEND_ID FROM FRIENDSHIP f WHERE f.USER_ID = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            int friendId = rs.getInt("FRIEND_ID");
            return getUserById(friendId);
        }, userId);
    }

    @Override
    public List<User> getCommonFriendsFromDB(int userId, int friendId)
    {
        String sql = "SELECT * FROM users " +
                "WHERE id IN (SELECT friend_id FROM friendship " +
                "WHERE user_id = ? INTERSECT " +
                "SELECT friend_id FROM friendship WHERE user_id = ?)";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            int commonFriendsId = rs.getInt("ID");
            return getUserById(commonFriendsId);
        }, userId, friendId);
    }

    @Override
    public boolean isUserExists(int userId) {
        String sql = "SELECT COUNT(*) FROM USERS WHERE ID = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count > 0;
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

    public void updateFriendshipStatus(int userId, int friendId) {
        String sql = "SELECT COUNT(*) AS count " +
                "FROM FRIENDSHIP " +
                "WHERE (USER_ID = ? AND FRIEND_ID = ?) OR (USER_ID = ? AND FRIEND_ID = ?)";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, userId, friendId, friendId, userId);

        String status = (count == 2) ? "confirmed" : "unconfirmed";
        sql = "UPDATE FRIENDSHIP SET STATUS = ? " +
                "WHERE (USER_ID = ? AND FRIEND_ID = ?) OR (USER_ID = ? AND FRIEND_ID = ?)";
        jdbcTemplate.update(sql, status, userId, friendId, friendId, userId);
    }

    private boolean areFriends(int userId, int friendId) {
        String sql = "SELECT COUNT(*) FROM FRIENDSHIP WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, userId, friendId, friendId, userId);
        return count > 0;
    }

}
