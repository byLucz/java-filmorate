package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {

    private final JdbcTemplate jdbcTemplate;

    private UserDbStorage userDbStorage;

    @BeforeEach
    void setUp() {
        userDbStorage = new UserDbStorage(jdbcTemplate);
    }

    @Test
    void testCreateUser() {
        User newUser = new User(1, "test@email.ru", "test_user", "Test User", LocalDate.of(1990, 1, 1));

        User createdUser = userDbStorage.createUsers(newUser);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isNotNull();
    }

    @Test
    void testUpdateUser() {
        User newUser = new User(null, "test@email.ru", "test_user", "Test User", LocalDate.of(1990, 1, 1));
        User createdUser = userDbStorage.createUsers(newUser);
        createdUser.setName("Updated Test User");

        User updatedUser = userDbStorage.updateUsers(createdUser);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getName()).isEqualTo("Updated Test User");
    }

    @Test
    void testGetAllUsers() {
        List<User> usersBefore = userDbStorage.allUsers();

        User newUser = new User(1, "test@email.ru", "test_user", "Test User", LocalDate.of(1990, 1, 1));
        userDbStorage.createUsers(newUser);

        List<User> usersAfter = userDbStorage.allUsers();

        assertThat(usersAfter.size()).isEqualTo(usersBefore.size() + 1);
    }

    @Test
    void testGetUsersMap() {
        User newUser = new User(2, "test@email.ru", "test_user", "Test User", LocalDate.of(1990, 1, 1));
        userDbStorage.createUsers(newUser);

        Map<Integer, User> userMap = userDbStorage.getUsers();

        assertThat(userMap).containsKey(newUser.getId());
    }
}