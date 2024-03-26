package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@JdbcTest // указываем, о необходимости подготовить бины для работы с БД
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

        private final JdbcTemplate jdbcTemplate;

        @Test
        public void testFindUserById() {
            // Подготавливаем данные для теста
            User newUser = new User(1, "user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
            UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
            userStorage.createUsers(newUser);

            // вызываем тестируемый метод
            User savedUser = userStorage.allUsers().get(0);

            // проверяем утверждения
            assertThat(savedUser)
                    .isNotNull() // проверяем, что объект не равен null
                    .usingRecursiveComparison() // проверяем, что значения полей нового
                    .isEqualTo(newUser);        // и сохраненного пользователя - совпадают
        }


}