package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.FilmServiceLogic;
import ru.yandex.practicum.filmorate.storage.event.EventRepositoryImpl;
import ru.yandex.practicum.filmorate.storage.film.FilmRepositoryImpl;
import ru.yandex.practicum.filmorate.storage.user.UserRepositoryImpl;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@JdbcTest
class FilmControllerTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    FilmController filmController;
    Film film;
    Validator validator;

    @BeforeEach
    void create() {
        filmController = new FilmController(new FilmServiceLogic(new FilmRepositoryImpl(jdbcTemplate),
               new UserRepositoryImpl(jdbcTemplate), new EventRepositoryImpl(jdbcTemplate)));
        MPA mpa = MPA.builder()
                .id(1)
                .name("G")
                .build();
        film = Film.builder()
                .name("nameFilm")
                .description("description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(80)
                .likes(new HashSet<>())
                .genres(new TreeSet<>())
                .directors(new TreeSet<>())
                .mpa(mpa)
                .build();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @Sql({"/schema.sql"})
    @Sql({"/data.sql"})
    void checkTestMapping() {
        Film checkFilm = filmController.createFilm(film);
        assertEquals(1, checkFilm.getId(), "Не записывается id");

        Film checkFilm2 = filmController.createFilm(film);
        assertEquals(2, checkFilm2.getId(), "Не обновляется id");

        film.setId(1);
        film.setName("UpdName");
        film.setDescription("Update");
        filmController.updateFilm(film);
        assertEquals(film, filmController.getFilms().get(0), "Не корректно обновляет film");

        List<Film> list = filmController.getFilms();
        assertEquals(2, list.size(), "Не сохраняет фильмы");
    }

    @Test
    @Sql({"/schema.sql"})
    @Sql({"/data.sql"})
    void checkValidationMethodFilmController() {
        film.setName(" ");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Пропустил пустое название");

        film.setName("nameFilm");
        film.setReleaseDate(LocalDate.of(1500, 1, 1));
        violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Фильм с невозможной датой релиза");

        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(-3);
        violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Продолжительность фильма должна быть положительной");
    }

}