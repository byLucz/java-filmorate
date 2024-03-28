package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

public interface RatingStorage {
    List<MPA> allRatings();

    MPA findRatingById(int id);

}
