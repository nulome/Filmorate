package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class RelatingController {
    private final FilmService filmServiceLogic;

    @GetMapping("/genres")
    public List<Genre> getGenres() {
        return filmServiceLogic.getGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenre(@PathVariable Integer id) {
        return filmServiceLogic.getGenre(id);
    }

    @GetMapping("/mpa")
    public List<MPA> getMpas() {
        return filmServiceLogic.getMpas();
    }

    @GetMapping("/mpa/{id}")
    public MPA getMpa(@PathVariable Integer id) {
        return filmServiceLogic.getMpa(id);
    }

}
