package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Data
public class Director implements Comparable<Director> {
    @NotNull
    int id;
    @NotBlank
    String name;

    @Override
    public int compareTo(Director o) {
        return this.id - o.id;
    }
}
