package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public void addFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        userStorage.addFriend(userId,friendId);
        log.info("Пользователь {} успешно добавлен в друзья к пользователю {}", friend.getName(), user.getName());
    }


    public void deleteFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        userStorage.deleteFriend(userId,friendId);
        log.info("Пользователь {} успешно удален из друзей пользователя {}", friend.getName(), user.getName());
    }

    public List<User> getFriendsList(int userId) {
        User user = userStorage.getUserById(userId);
        return userStorage.getFriendsList(userId);
    }

    public List<User> getCommonFriends(int userId, int friendId) {
        if (!userStorage.isUserExists(userId) || !userStorage.isUserExists(friendId)) {
            throw new NotFoundException("Такого пользователя или друга не существует");
        }
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        log.info("Выведен список общих друзей для пользователей {} и {}", friend.getName(), user.getName());
        return userStorage.getCommonFriendsFromDB(userId,friendId);
    }

}
