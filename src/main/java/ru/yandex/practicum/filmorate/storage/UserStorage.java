package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {
    List<User> allUsers();

    User createUsers(User user);

    User updateUsers(User user);

    void validateUser(User user);
    Map<Integer, User> getUsers();

}
