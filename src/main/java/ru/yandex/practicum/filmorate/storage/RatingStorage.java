package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

public interface RatingStorage {
    List<Rating> allRatings();

    Rating findRatingById(int id);

}
