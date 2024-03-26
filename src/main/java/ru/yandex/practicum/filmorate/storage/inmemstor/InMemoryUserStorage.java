package ru.yandex.practicum.filmorate.storage.inmemstor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Component
@Qualifier("inMemoryUserStorage")
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private Map<Integer, User> users = new HashMap<>();
    private int userId = 1;

    @Override
    public List<User> allUsers() {
        log.info("Выведен список всех пользователей");
        return new ArrayList<>(users.values());
    }

    @Override
    public User createUsers(User user) {
        validateUser(user);
        user.setId(userId);
        if (user.getFriends() == null)
            user.setFriends(new HashSet<>());
        users.put(user.getId(), user);
        log.info("Добавлен пользователь " + user.getLogin());
        userId++;
        return user;
    }

    @Override
    public User updateUsers(User user) {
        validateUser(user);
        if (!users.containsKey(user.getId())) {
            log.warn("Такого пользователя не существует " + user.getName());
            throw new NotFoundException("Пользователя не найдено");
        }
        if (user.getFriends() == null)
            user.setFriends(new HashSet<>());
        users.put(user.getId(), user);
        log.info("Обновлен пользователь " + user.getName());
        return user;
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

    public Map<Integer, User> getUsers() {
        return users;
    }
}
