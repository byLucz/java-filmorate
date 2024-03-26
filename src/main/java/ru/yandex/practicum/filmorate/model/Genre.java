package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotBlank;

@Data
@RequiredArgsConstructor
public class Genre {
    @Autowired
    private Integer id;
    @NotBlank
    private String name;

    public Genre(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
