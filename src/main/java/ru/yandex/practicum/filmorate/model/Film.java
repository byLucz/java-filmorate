package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Data
@Builder
public class Film {
    private int id;
    @NotBlank
    private String name;
    private String description;
    private LocalDate releaseDate;
    @Positive
    private int duration;
}
