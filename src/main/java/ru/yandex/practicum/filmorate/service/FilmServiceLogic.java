package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.related.Constants;
import ru.yandex.practicum.filmorate.related.UnknownValueException;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class FilmServiceLogic implements FilmService {

    private final FilmStorage dataFilmStorage;
    private final UserStorage dataUserStorage;
    private final EventStorage eventStorage;

    @Override
    public Film createFilm(Film film) {
        log.info("Получен запрос Post /films - {}", film.getName());
        return dataFilmStorage.createFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        log.info("Получен запрос Put /films - {}", film.getName());
        checkAndProvideFilmInDataBase(film.getId());
        return dataFilmStorage.updateFilm(film);
    }

    @Override
    public Film deleteFilm(Integer filmId) {
        log.info("Получен запрос DELETE /films/{}", filmId);
        checkAndProvideFilmInDataBase(filmId);
        return dataFilmStorage.deleteFilm(filmId);
    }

    @Override
    public List<Film> getFilms() {
        log.trace("Получен запрос Get /films");
        return dataFilmStorage.getFilms();
    }

    @Override
    public Set<Integer> addLikes(Integer filmId, Integer userId) {
        log.debug("Получен запрос PUT /films/{}/like/{} - лайк фильму", filmId, userId);
        checkAndReceiptUserInDataBase(userId);
        Film film = checkAndProvideFilmInDataBase(filmId);
        film.getLikes().add(userId);
        dataFilmStorage.updateFilm(film);
        eventStorage.addLikesHandler(filmId, userId, System.currentTimeMillis());
        return film.getLikes();
    }

    @Override
    public Set<Integer> deleteLikes(Integer filmId, Integer userId) {
        log.debug("Получен запрос DELETE /films/{}/friends/{} - удаление лайка", filmId, userId);
        checkAndReceiptUserInDataBase(userId);
        Film film = checkAndProvideFilmInDataBase(filmId);
        film.getLikes().remove(userId);
        dataFilmStorage.updateFilm(film);
        eventStorage.deleteLikesHandler(filmId, userId, System.currentTimeMillis());
        return film.getLikes();
    }

    @Override
    public List<Film> getPopularMovies(Integer count) {
        log.trace("Получен запрос GET /films/popular?count={} - топ по лайкам", count);
        if (count == null) {
            count = Constants.DEFAULT_POPULAR_VALUE;
        }
        List<Film> films = dataFilmStorage.getFilms();
        return films.stream()
                .sorted(this::comparePopularMovies)
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> getFilmsSortToDirector(Integer directorId, String sortBy) {
        log.trace("Получен запрос GET /films/director/{}?sortBy={} ", directorId, sortBy);
        List<Film> films = dataFilmStorage.getFilmsToDirector(directorId);
        return films.stream()
                .sorted(compareSortToDirector(sortBy))
                .collect(Collectors.toList());
    }

    @Override
    public Film getFilm(Integer id) {
        log.trace("Получен запрос GET /films/{}", id);
        return checkAndProvideFilmInDataBase(id);
    }

    @Override
    public List<Genre> getGenres() {
        return dataFilmStorage.getGenres();
    }

    @Override
    public Genre getGenre(Integer id) {
        return dataFilmStorage.getGenre(id);
    }

    @Override
    public List<MPA> getMpas() {
        return dataFilmStorage.getMpas();
    }

    @Override
    public MPA getMpa(Integer id) {
        return dataFilmStorage.getMpa(id);
    }

    @Override
    public List<Film> getFilmsBySearch(String query, String bySearch) {
        log.debug("Получен запрос GET /films/search?query={}&by={}", query, bySearch);
        List<Film> films =  dataFilmStorage.getFilmsBySearch(query, bySearch);
        return films.stream()
                .sorted(this::comparePopularMovies)
                .collect(Collectors.toList());
    }

    private int comparePopularMovies(Film film1, Film film2) {
        return film2.getLikes().size() - film1.getLikes().size();
    }

    private Comparator<Film> compareSortToDirector(String sort) {
        switch (sort) {
            case "year":
                return Comparator.comparingInt(f -> f.getReleaseDate().getYear());
            case "likes":
                return Comparator.comparingInt(f -> f.getLikes().size());
        }

        log.error("Ошибка в способе сортировки: {}", sort);
        throw new UnknownValueException("Передано не верное значение sortBy: " + sort);
    }

    private Film checkAndProvideFilmInDataBase(Integer id) {
        try {
            return dataFilmStorage.getFilm(id);
        } catch (EmptyResultDataAccessException e) {
            log.error("Ошибка в запросе к базе данных. Не найдено значение по id: {} \n {}", id, e.getMessage());
            throw new UnknownValueException("Не верный id фильма: " + id);
        }
    }

    private User checkAndReceiptUserInDataBase(Integer id) {
        try {
            return dataUserStorage.getUser(id);
        } catch (EmptyResultDataAccessException e) {
            log.error("Ошибка в запросе к базе данных. Не найдено значение по id: {} \n {}", id, e.getMessage());
            throw new UnknownValueException("Передан не верный id: " + id);
        }
    }
}
