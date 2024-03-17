package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(int userId, int friendId) {
        User user = userStorage.allUsers().get(--userId);
        User friend = userStorage.allUsers().get(--friendId);
        Set<Integer> userFriends = user.getFriends();
        Set<Integer> friendFriends = friend.getFriends();
        if (userFriends == null)
            userFriends = new HashSet<>();
        userFriends.add(++friendId);

        if (friendFriends == null)
            friendFriends = new HashSet<>();
        friendFriends.add(++userId);

        //user.setFriends(userFriends);
        //friend.setFriends(friendFriends);
        log.info("Пользователь {} успешно добавлен в друзья пользователю {}",friend.getName(),user.getName());
    }

    public void deleteFriend(int userId, int friendId) {
        User user = userStorage.allUsers().get(--userId);
        User friend = userStorage.allUsers().get(--friendId);
        Set<Integer> userFriends = user.getFriends();
        Set<Integer> friendFriends = friend.getFriends();
        userFriends.remove(++friendId);
        friendFriends.remove(++userId);

        //user.setFriends(userFriends);
        //friend.setFriends(friendFriends);
        log.info("Пользователь {} успешно удален из друзей пользователя {}",friend.getName(),user.getName());
    }

    public List<User> getFriends(int userId) {
        List<User> friends = new ArrayList<>();
        User user = userStorage.allUsers().get(--userId);
        for (Integer spottedId : user.getFriends())
            friends.add(userStorage.allUsers().get(--spottedId));
        log.info("Выведен список друзей пользователя {}",user.getName());
        return friends;
    }

    public List<User> getCommonFriends(int userId, int friendId) {
        List<User> commonFriends = new ArrayList<>();
        User user = userStorage.allUsers().get(--userId);
        User friend = userStorage.allUsers().get(--friendId);
        Set<Integer> friendList = new HashSet<>(user.getFriends());

        friendList.retainAll(friend.getFriends());

        for (Integer spottedId : friendList)
            commonFriends.add(userStorage.allUsers().get(--spottedId));
        log.info("Выведен список общих друзей для пользователей {} и {}",friend.getName(),user.getName());
        return commonFriends;
    }
}
