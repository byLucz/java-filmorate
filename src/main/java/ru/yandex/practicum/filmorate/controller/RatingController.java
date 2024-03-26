package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.RatingDbStorage;

import java.util.List;

@RestController
@RequestMapping("/ratings")
public class RatingController {
    private final RatingDbStorage ratingStorage;

    public RatingController(RatingDbStorage ratingStorage) {
        this.ratingStorage = ratingStorage;
    }

    @GetMapping
    public List<Rating> allRatings() {
        return ratingStorage.allRatings();
    }

    @GetMapping("/{id}")
    public Rating findRatingById(@PathVariable Integer id) {
        return ratingStorage.findRatingById(id);
    }
}
