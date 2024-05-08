package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorServiceLogic;

//    GET /directors - Список всех режиссёров
//
//    GET /directors/{id}- Получение режиссёра по id
//
//    POST /directors - Создание режиссёра
//
//    PUT /directors - Изменение режиссёра
//
//     DELETE /directors/{id} - Удаление режиссёра

    @GetMapping
    public List<Director> getDirectors() {
        return null;
    }

    @GetMapping("/{id}")
    public Director getDirector(@PathVariable int id) {
        return null;
    }

    @PostMapping
    public Director createDirector(@RequestBody @Valid Director director) {
        return null;
    }

    @PutMapping
    public Director updateDirector(@RequestBody @Valid Director director) {
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable int id) {
    }
}
