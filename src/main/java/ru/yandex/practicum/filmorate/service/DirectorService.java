package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorService {
    List<Director> getDirectors();

    Director getDirector(Integer id);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(Integer id);
}
