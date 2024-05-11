package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Builder
@Data
public class Review {
    @JsonProperty("reviewId")
    int id;
    @NotNull
    String content;
    @NotNull
    @JsonProperty("isPositive")
    Boolean isPositive;
    @NotNull
    Integer filmId;
    @NotNull
    Integer userId;
    int useful;
}
