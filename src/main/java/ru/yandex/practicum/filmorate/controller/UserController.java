package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private Map<Integer, User> users = new HashMap<>();
    private int userId = 1;

    @GetMapping
    public List<User> allUsers() {
        log.info("Выведен список всех пользователей");
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        validateUser(user);
        user.setId(userId);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь " + user.getLogin());
        userId++;
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        validateUser(user);
        if(!users.containsKey(user.getId())) {
            log.warn("Такого пользователя не существует " + user.getName());
            throw new NotFoundException("Пользователя не найдено");
        }
        users.put(user.getId(), user);
        log.info("Обновлен пользователь " + user.getName());
        return user;
    }

    private void validateUser(User user)
    {
        if(user.getName() == null || user.getName().isBlank()) {
            log.info("Пустое имя было заменено на логин");
            user.setName(user.getLogin());
        }
        if(user == null) {
            log.warn("Информации о пользователе не предоставлено");
            throw new ValidationException("Информации о пользователе не предоставлено");
        }
    }
}