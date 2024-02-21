package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController userController;
    private User user;

    private Validator validator;

    @BeforeEach
    void prepareData() {
        userController = new UserController();

        user = User.builder()
                .email("lucz@loissquad.ru")
                .login("lucz")
                .name("L")
                .birthday(LocalDate.of(2000, 1, 21))
                .build();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldReplaceNameToLogin() {
        user.setName("");

        userController.createUser(user);
        List<User> users = userController.allUsers();

        assertEquals(user.getName(), user.getLogin());
        assertEquals(user, users.get(0));
    }

    @Test
    void shouldntPassFutureBirthday() {
        user.setBirthday(LocalDate.of(2111,12,12));

        userController.createUser(user);
        List<User> users = userController.allUsers();
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldntPassInvalidEmail() {
        user.setEmail("chippichipimail.ru");

        userController.createUser(user);
        List<User> users = userController.allUsers();
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldAddValidUser() {
        userController.createUser(user);
        List<User> users = userController.allUsers();

        assertEquals(1,users.size());
        assertEquals(user, users.get(0));
    }


}