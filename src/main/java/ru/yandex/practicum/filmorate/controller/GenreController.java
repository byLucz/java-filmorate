package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;

import java.util.List;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreDbStorage genreStorage;

    @GetMapping
    public List<Genre> allGenres() {
        return genreStorage.allGenres();
    }

    @GetMapping("/{id}")
    public Genre findGenreById(@PathVariable Integer id) {
        return genreStorage.findByGenreId(id);
    }
}
