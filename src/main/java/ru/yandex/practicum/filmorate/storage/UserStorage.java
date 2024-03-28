package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> allUsers();

    User createUsers(User user);

    User updateUsers(User user);

    User getUserById(int id);

    void addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    List<User> getFriendsList(int userId);

    List<User> getCommonFriendsFromDB(int userId, int friendId);

    boolean isUserExists(int userId);
}
