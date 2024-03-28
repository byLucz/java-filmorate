package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.RatingDbStorage;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class RatingController {
    private final RatingDbStorage ratingStorage;

    @GetMapping
    public List<MPA> allRatings() {
        return ratingStorage.allRatings();
    }

    @GetMapping("/{id}")
    public MPA findRatingById(@PathVariable Integer id) {
        return ratingStorage.findRatingById(id);
    }
}
