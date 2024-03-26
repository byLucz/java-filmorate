package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import javax.validation.constraints.NotBlank;

@Data
@RequiredArgsConstructor
public class MPA {
    @Autowired
    private Integer id;
    @NotBlank
    private String ratingCode;
    private String description;


    public MPA(Integer id, String ratingCode, String description) {
        this.id = id;
        this.ratingCode = ratingCode;
        this.description = description;
    }
}
