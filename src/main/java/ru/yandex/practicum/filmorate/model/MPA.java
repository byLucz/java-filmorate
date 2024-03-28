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
    private String name;
    private String description;


    public MPA(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
