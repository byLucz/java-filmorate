package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@RequiredArgsConstructor
public class Film {
    @Autowired
    private Integer id;
    private Set<Integer> likeCounter;
    @NotBlank
    private String name;
    private String description;
    private LocalDate releaseDate;
    @Positive
    private int duration;

    private MPA mpa;

    private LinkedHashSet<Genre> genres = new LinkedHashSet<>();

    public Film(Integer id, String name, String description, LocalDate releaseDate, int duration, MPA mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
    }
}