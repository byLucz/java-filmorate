package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

        private final JdbcTemplate jdbcTemplate;

        private FilmDbStorage filmDbStorage;

        @BeforeEach
        void setUp() {
            filmDbStorage = new FilmDbStorage(jdbcTemplate);
        }

        @Test
        public void testFindUserById() {
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

        @Test
        void testCreateFilm() {
            Film newFilm = new Film(null, "Test Film", "Description", LocalDate.now(), 120, new MPA(1, "PG-13", "1131313131313131313"));
            LinkedHashSet<Genre> genres = new LinkedHashSet<>();
            genres.add(new Genre(1, "Драма"));
            genres.add(new Genre(2, "Приключения"));
            newFilm.setGenres(genres);

            Film createdFilm = filmDbStorage.createFilm(newFilm);

            assertThat(createdFilm).isNotNull();
            assertThat(createdFilm.getId()).isNotNull();
        }

        @Test
        void testUpdateFilm() {
            Film newFilm = new Film(null, "Test Film", "Description", LocalDate.now(), 120, new MPA(1, "PG-13", "1131313131313131313"));
            LinkedHashSet<Genre> genres = new LinkedHashSet<>();
            genres.add(new Genre(1, "Драма"));
            genres.add(new Genre(2, "Приключения"));
            newFilm.setGenres(genres);
            Film createdFilm = filmDbStorage.createFilm(newFilm);

            createdFilm.setName("Test Film2");

            Film updatedFilm = filmDbStorage.updateFilm(createdFilm);

            assertThat(updatedFilm).isNotNull();
            assertThat(updatedFilm.getName()).isEqualTo("Test Film2");
        }

        @Test
        void testAllFilms() {
            Film newFilm = new Film(null, "Test Film", "Description", LocalDate.now(), 120, new MPA(1, "PG-13", "1131313131313131313"));
            LinkedHashSet<Genre> genres = new LinkedHashSet<>();
            genres.add(new Genre(1, "Драма"));
            genres.add(new Genre(2, "Приключения"));
            newFilm.setGenres(genres);
            Film createdFilm = filmDbStorage.createFilm(newFilm);

            List<Film> films = filmDbStorage.allFilms();

            assertThat(films).isNotNull();
            assertThat(films.size()).isGreaterThan(0);
        }



}