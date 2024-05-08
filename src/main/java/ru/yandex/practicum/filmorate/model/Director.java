package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Builder
@Data
public class Director implements Comparable<Director> {
    int id;
    @NotBlank
    String name;

    @Override
    public int compareTo(Director o) {
        return this.id - o.id;
    }
}
