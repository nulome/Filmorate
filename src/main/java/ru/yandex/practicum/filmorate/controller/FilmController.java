package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmServiceLogic;

    @PostMapping
    public Film createFilm(@RequestBody @Valid Film film) {
        return filmServiceLogic.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        return filmServiceLogic.updateFilm(film);
    }

    @DeleteMapping("/{filmId}")
    public Film deleteFilm(@PathVariable Integer filmId) {
        return filmServiceLogic.deleteFilm(filmId);
    }

    @GetMapping
    public List<Film> getFilms() {
        return filmServiceLogic.getFilms();
    }

    @GetMapping("/{filmId}")
    public Film getFilm(@PathVariable int filmId) {
        return filmServiceLogic.getFilm(filmId);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public Set<Integer> addLikes(@PathVariable int filmId, @PathVariable int userId) {
        return filmServiceLogic.addLikes(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Set<Integer> deleteLikes(@PathVariable int filmId, @PathVariable int userId) {
        return filmServiceLogic.deleteLikes(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularMovies(@RequestParam(name = "count", required = false) Integer count,
                                       @RequestParam(name = "genreId", required = false) Integer genreId,
                                       @RequestParam(name = "year", required = false) Integer year) {
        return filmServiceLogic.getPopularMovies(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsSortToDirector(@PathVariable int directorId, @RequestParam String sortBy) {
        return filmServiceLogic.getFilmsSortToDirector(directorId, sortBy);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam Integer userId, @RequestParam Integer friendId) {
        return filmServiceLogic.getCommonFilms(userId, friendId);
    }

    @GetMapping("/search")
    public List<Film> getFilmsBySearch(@RequestParam String query, @RequestParam String by) {
        return filmServiceLogic.getFilmsBySearch(query, by);
    }
}
