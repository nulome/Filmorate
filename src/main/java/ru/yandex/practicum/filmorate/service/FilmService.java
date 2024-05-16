package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;
import java.util.Set;

public interface FilmService {
    Film createFilm(Film film);

    Film updateFilm(Film film);

    Film deleteFilm(Integer filmId);

    List<Film> getFilms();

    Set<Integer> addLikes(Integer filmId, Integer userId);

    Set<Integer> deleteLikes(Integer filmId, Integer userId);

    List<Film> getPopularMovies(Integer count, Integer genreId, Integer year);

    List<Film> getFilmsSortToDirector(Integer directorId, String sortBy);

    Film getFilm(Integer id);

    List<Genre> getGenres();

    Genre getGenre(Integer id);

    List<MPA> getMpas();

    MPA getMpa(Integer id);

    List<Film> getCommonFilms(Integer userId, Integer friendId);

    List<Film> getFilmsBySearch(String query, String bySearch);
}
