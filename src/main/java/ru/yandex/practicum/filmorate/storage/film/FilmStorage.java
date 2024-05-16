package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

public interface FilmStorage {
    Film createFilm(Film film);

    Film updateFilm(Film film);

    Film deleteFilm(Integer filmId);

    List<Film> getFilms();

    Film getFilm(Integer id);

    List<Genre> getGenres();

    Genre getGenre(Integer id);

    List<MPA> getMpas();

    MPA getMpa(Integer id);

    List<Film> getFilmsToDirector(Integer directorId);

    List<Film> getCommonFilms(Integer userId, Integer friendId);

    List<Film> getFilmsBySearch(String query, String bySearch);
}
