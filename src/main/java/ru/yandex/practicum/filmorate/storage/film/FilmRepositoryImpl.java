package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.related.UnknownValueException;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Slf4j
@RequiredArgsConstructor
public class FilmRepositoryImpl implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_FILM_BASE_SQL = "SELECT f.id, f.name, f.description, f.releasedate, f.duration, " +
            "l.user_id AS likes, fg.genre_id, g.name AS genre_name, fm.mpa_id, m.name AS mpa_name, " +
            "d.id AS director_id, d.name AS director_name " +
            "FROM films f " +
            "LEFT JOIN film_genres fg ON f.id = fg.film_id " +
            "LEFT JOIN genre g ON fg.genre_id = g.id " +
            "LEFT JOIN film_mpa fm ON f.id = fm.film_id " +
            "LEFT JOIN mpa m ON fm.mpa_id = m.id " +
            "LEFT JOIN film_directors fd ON fd.film_id = f.id " +
            "LEFT JOIN director d ON d.id = fd.director_id " +
            "LEFT JOIN likes l ON f.id = l.film_id ";

    private static final String UPDATE_FILM_SQL = "UPDATE films SET name = ?, description  = ?, releasedate  = ?, " +
            "duration = ? WHERE id = ?";

    private static final String DELETE_FILM_SQL = "DELETE FROM films WHERE id = ?";
    private static final String SELECT_ALL_FILMS_SQL = SELECT_FILM_BASE_SQL + "ORDER BY f.id";


    private static final String SELECT_FILM_BY_ID_SQL = SELECT_FILM_BASE_SQL + "WHERE f.id = ? ORDER BY f.id";

    private static final String SELECT_ALL_GENRES_SQL = "SELECT g.id AS genre_id, g.name AS genre_name FROM genre g " +
            "ORDER BY g.id";

    private static final String SELECT_GENRE_BY_ID_SQL = "SELECT g.id AS genre_id, g.name AS genre_name FROM genre g " +
            "WHERE id = ?";

    private static final String SELECT_ALL_MPAS_SQL = "SELECT m.id AS mpa_id, m.name AS mpa_name FROM mpa m " +
            "ORDER BY m.id";

    private static final String SELECT_MPA_BY_ID_SQL = "SELECT m.id AS mpa_id, m.name AS mpa_name FROM mpa m WHERE id = ?";

    private static final String SELECT_FILMS_TO_DIRECTOR_SQL = SELECT_FILM_BASE_SQL + "WHERE director_id = ? ORDER BY f.id";

    private static final String SELECT_COMMON_FILMS_SQL = SELECT_FILM_BASE_SQL + "WHERE f.id IN (SELECT f2.id " +
            "FROM FILMS f2 " +
            "INNER JOIN LIKES l ON f.ID = l.FILM_ID AND l.USER_ID = ? " +
            "INNER JOIN LIKES l2 ON f.ID = l2.FILM_ID AND l2.USER_ID = ?)";


    @Override
    public Film createFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        Map<String, String> params = Map.of("name", film.getName(), "description", film.getDescription(),
                "releasedate", film.getReleaseDate().toString(), "duration", film.getDuration().toString());
        Number id = simpleJdbcInsert.executeAndReturnKey(params);
        film.setId(id.intValue());
        updMpaAndGenreAndLikeInDataBase(film);
        updDirectorInDataBase(film);
        return getFilm(id.intValue());
    }

    @Override
    public Film updateFilm(Film film) {
        jdbcTemplate.update(UPDATE_FILM_SQL, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getId());
        updMpaAndGenreAndLikeInDataBase(film);
        updDirectorInDataBase(film);
        return getFilm(film.getId());
    }

    @Override
    public Film deleteFilm(Integer filmId) {
        Film film = getFilm(filmId);
        jdbcTemplate.update(DELETE_FILM_SQL, filmId);
        return film;
    }

    @Override
    public List<Film> getFilms() {
        List<Film> films = null;
        try {
            films = jdbcTemplate.queryForObject(SELECT_ALL_FILMS_SQL, mapperListAllFilms());
        } catch (EmptyResultDataAccessException e) {
            log.error("Error. Пустой результат от базы данных. Возврат пустого списка.");
        } finally {
            if (films == null) {
                return Collections.emptyList();
            }
            return films;
        }
    }

    @Override
    public Film getFilm(Integer id) {
        return jdbcTemplate.queryForObject(SELECT_FILM_BY_ID_SQL, this::mapperFilm, id);
    }

    @Override
    public List<Genre> getGenres() {
        return jdbcTemplate.queryForObject(SELECT_ALL_GENRES_SQL,
                (rs, rowNum) -> {
                    List<Genre> genresList = new ArrayList<>();
                    if (rs.getInt("genre_id") == 0) {
                        return genresList;
                    }
                    do {
                        genresList.add(createGenreBuilder(rs));
                    } while (rs.next());
                    return genresList;
                });
    }

    @Override
    public Genre getGenre(Integer id) {
        try {
            return jdbcTemplate.queryForObject(SELECT_GENRE_BY_ID_SQL,
                    (rs, rowNum) -> createGenreBuilder(rs), id);
        } catch (EmptyResultDataAccessException e) {
            log.error("Ошибка в запросе к базе данных. Не верный id: {} \n {}", id, e.getMessage());
            throw new UnknownValueException("Не верный id жанра: " + id);
        }
    }

    @Override
    public List<MPA> getMpas() {
        return jdbcTemplate.queryForObject(SELECT_ALL_MPAS_SQL,
                (rs, rowNum) -> {
                    List<MPA> mpaList = new ArrayList<>();
                    if (rs.getInt("mpa_id") == 0) {
                        return mpaList;
                    }
                    do {
                        mpaList.add(createMpaBuilder(rs));
                    } while (rs.next());
                    return mpaList;
                });
    }

    @Override
    public MPA getMpa(Integer id) {
        try {
            return jdbcTemplate.queryForObject(SELECT_MPA_BY_ID_SQL,
                    (rs, rowNum) -> createMpaBuilder(rs), id);
        } catch (EmptyResultDataAccessException e) {
            log.error("Ошибка в запросе к базе данных. Не верный id: {} \n {}", id, e.getMessage());
            throw new UnknownValueException("Не верный id рейтинга: " + id);
        }
    }

    @Override
    public List<Film> getFilmsToDirector(Integer directorId) {
        return jdbcTemplate.queryForObject(SELECT_FILMS_TO_DIRECTOR_SQL, mapperListAllFilms(), directorId);
    }

    @Override
    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        List<Film> films = null;
        try {
            films = jdbcTemplate.queryForObject(SELECT_COMMON_FILMS_SQL, mapperListAllFilms(), userId, friendId);
        } catch (EmptyResultDataAccessException e) {
            log.error("Error. Пустой результат от базы данных. Возврат пустого списка.");
        } finally {
            if (films == null) {
                return Collections.emptyList();
            }
            return films;
        }
    }

    @Override
    public List<Film> getFilmsBySearch(String query, String bySearch) {
        List<Film> films = null;
        try {
            List<String> listSearch = checkBySearch(bySearch);
            if (listSearch.size() == 2) {
                films = jdbcTemplate.queryForObject(sqlSearchCreate(listSearch), mapperListAllFilms(),
                        getQueryParam(query), getQueryParam(query));
            } else {
                films = jdbcTemplate.queryForObject(sqlSearchCreate(listSearch), mapperListAllFilms(), getQueryParam(query));
            }
        } catch (EmptyResultDataAccessException e) {
            log.error("Error. Пустой результат от базы данных. Возврат пустого списка.");
        } finally {
            if (films == null) {
                return Collections.emptyList();
            }
            return films;
        }
    }

    private String getQueryParam(String query) {
        return "%" + query + "%";
    }

    private String sqlSearchCreate(List<String> listSearch) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT f.id, f.name, f.description, f.releasedate, f.duration, " +
                "l.user_id AS likes, fg.genre_id, g.name AS genre_name, fm.mpa_id, m.name AS mpa_name, " +
                "d.id AS director_id, d.name AS director_name " +
                "FROM films f " +
                "LEFT JOIN film_genres fg ON f.id = fg.film_id " +
                "LEFT JOIN genre g ON fg.genre_id = g.id " +
                "LEFT JOIN film_mpa fm ON f.id = fm.film_id " +
                "LEFT JOIN mpa m ON fm.mpa_id = m.id " +
                "LEFT JOIN film_directors fd ON fd.film_id = f.id " +
                "LEFT JOIN director d ON d.id = fd.director_id " +
                "LEFT JOIN likes l ON f.id = l.film_id WHERE ");
        for (int i = 0; i < listSearch.size(); i++) {
            if (i > 0) {
                sb.append("OR ");
            }
            sb.append(listSearch.get(i));
        }
        return sb.toString();
    }

    private List<String> checkBySearch(String bySearch) {
        String[] search = bySearch.split(",");
        List<String> listSearch = new ArrayList<>();
        for (String str : search) {
            switch (str) {
                case "director":
                    listSearch.add("d.name ILIKE ? ");
                    break;
                case "title":
                    listSearch.add("f.name ILIKE ? ");
                    break;
                default:
                    log.error("Ошибка в поиске: {}", bySearch);
                    throw new UnknownValueException("Передано не верное значение bySearch: " + bySearch);
            }
        }
        return listSearch;
    }

    private Film mapperFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = createFilmBuilder(rs);
        do {
            addLikeAndGenreAndMpaInFilm(rs, film);
            addDirectorInFilm(rs, film);
        } while (rs.next());
        return film;
    }

    private RowMapper<List<Film>> mapperListAllFilms() {
        return (rs, rowNum) -> {
            List<Film> filmsList = new ArrayList<>();
            int check = -1;
            Film film = null;
            do {
                if (rs.getInt("id") != check) {
                    film = createFilmBuilder(rs);
                    addLikeAndGenreAndMpaInFilm(rs, film);
                    addDirectorInFilm(rs, film);

                    filmsList.add(film);
                    check = rs.getInt("id");
                } else {
                    addLikeAndGenreAndMpaInFilm(rs, film);
                    addDirectorInFilm(rs, film);
                }
            } while (rs.next());
            return filmsList;
        };
    }

    private Film createFilmBuilder(ResultSet rs) throws SQLException {
        return Film.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("releasedate").toLocalDate())
                .duration(rs.getInt("duration"))
                .likes(new HashSet<>())
                .genres(new TreeSet<>())
                .directors(new TreeSet<>())
                .build();
    }

    private Genre createGenreBuilder(ResultSet rs) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("genre_name"))
                .build();
    }

    private MPA createMpaBuilder(ResultSet rs) throws SQLException {
        return MPA.builder()
                .id(rs.getInt("mpa_id"))
                .name(rs.getString("mpa_name"))
                .build();
    }

    private void updDirectorInDataBase(Film film) {
        jdbcTemplate.update("DELETE FROM film_directors WHERE film_id = ?", film.getId());
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            for (Director director : film.getDirectors()) {
                jdbcTemplate.update("INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)",
                        film.getId(), director.getId());
            }
        }
    }

    private void updMpaAndGenreAndLikeInDataBase(Film film) {
        jdbcTemplate.update("DELETE FROM likes WHERE film_id = ?", film.getId());
        if (film.getLikes() != null && !film.getLikes().isEmpty()) {
            for (Integer like : film.getLikes()) {
                jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)", film.getId(), like);
            }
        }

        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)",
                        film.getId(), genre.getId());
            }
        }

        jdbcTemplate.update("DELETE FROM film_mpa WHERE film_id = ?", film.getId());
        if (film.getMpa() != null) {
            jdbcTemplate.update("INSERT INTO film_mpa (film_id, mpa_id) VALUES (?, ?)",
                    film.getId(), film.getMpa().getId());

        }
    }

    private void addLikeAndGenreAndMpaInFilm(ResultSet rs, Film film) throws SQLException {
        if (rs.getInt("likes") != 0) {
            film.getLikes().add(rs.getInt("likes"));
        }

        if (rs.getString("genre_name") != null) {
            film.getGenres().add(createGenreBuilder(rs));
        }

        if (film.getMpa() == null && rs.getInt("mpa_id") != 0) {
            film.setMpa(createMpaBuilder(rs));
        }
    }

    private void addDirectorInFilm(ResultSet rs, Film film) throws SQLException {
        if (rs.getInt("director_id") != 0) {
            film.getDirectors().add(DirectorStorage.createDirectorBuilder(rs));
        }
    }
}

