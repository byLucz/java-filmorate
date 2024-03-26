package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserService {
    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;

    public UserService(JdbcTemplate jdbcTemplate, @Qualifier("userDbStorage") UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }

    public void addFriend(int userId, int friendId) {
        if (!isUserExists(userId) || !isUserExists(friendId)) {
            throw new NotFoundException("Такого пользователя или друга не существует");
        }
        if (areFriends(userId, friendId)) {
            throw new ValidationException("Пользователи уже являются друзьями");
        }
        updateFriendshipStatus(userId,friendId);
        String sql = "INSERT INTO FRIENDSHIP (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
        log.info("Пользователь с id={} успешно добавлен в друзья к пользователю с id={}", friendId, userId);
    }


    public void deleteFriend(int userId, int friendId) {
        if (!isUserExists(userId) || !isUserExists(friendId)) {
            throw new NotFoundException("Такого пользователя или друга не существует");
        }
        String deleteSql = "DELETE FROM FRIENDSHIP WHERE USER_ID=" + userId + " and FRIEND_ID=" + friendId;
        jdbcTemplate.execute(deleteSql);
        updateFriendshipStatus(userId,friendId);

        log.info("Пользователь {} успешно удален из друзей пользователя {}",friendId,userId);
    }

    public List<User> getFriendsList(int userId){
        String sql = "SELECT f.FRIEND_ID FROM FRIENDSHIP f WHERE f.USER_ID = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            int friendId = rs.getInt("FRIEND_ID");
            return userStorage.getUsers().get(friendId);
        }, userId);
    }

    public List<User> getCommonFriends(int userId, int friendId) {
        if (!isUserExists(userId) || !isUserExists(friendId)) {
            throw new NotFoundException("Такого пользователя или друга не существует");
        }

        List<User> commonFriends = new ArrayList<>();
        User user = userStorage.getUsers().get(userId);
        User friend = userStorage.getUsers().get(friendId);

        Set<Integer> commonFriendIds = new HashSet<>(user.getFriends());
        commonFriendIds.retainAll(friend.getFriends());

        Set<Integer> addedFriendIds = new HashSet<>();

        for (Integer commonFriendId : commonFriendIds) {
            if (!addedFriendIds.contains(commonFriendId)) {
                commonFriends.add(userStorage.getUsers().get(commonFriendId));
                addedFriendIds.add(commonFriendId);
            }
        }

        log.info("Выведен список общих друзей для пользователей {} и {}", friend.getName(), user.getName());
        return commonFriends;
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
    public boolean isUserExists(int userId) {
        String sql = "SELECT COUNT(*) FROM USERS WHERE ID = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count > 0;
    }

    private boolean areFriends(int userId, int friendId) {
        String sql = "SELECT COUNT(*) FROM FRIENDSHIP WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, userId, friendId, friendId, userId);
        return count > 0;
    }
}
