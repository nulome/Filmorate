package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
@RequiredArgsConstructor
public class DirectorRepositoryImpl implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Director> getDirectors() {
        return jdbcTemplate.queryForObject("SELECT d.id AS director_id, d.name AS director_name" +
                        " FROM director d ORDER BY director_id", mapperListAllDirectors());
    }

    @Override
    public Director getDirector(Integer id) {
        return jdbcTemplate.queryForObject("SELECT d.id AS director_id, d.name AS director_name" +
                        " FROM director d WHERE id = ?", (rs, rowNum) -> DirectorStorage.createDirectorBuilder(rs), id);
    }

    @Override
    public Director createDirector(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("director")
                .usingGeneratedKeyColumns("id");
        Map<String, String> params = Map.of("name", director.getName());
        Number id = simpleJdbcInsert.executeAndReturnKey(params);

        return getDirector(id.intValue());
    }

    @Override
    public Director updateDirector(Director director) {
        jdbcTemplate.update("UPDATE director SET name = ?" +
                " WHERE id = ?", director.getName(), director.getId());
        return getDirector(director.getId());
    }

    @Override
    public void deleteDirector(Integer id) {
        jdbcTemplate.update("DELETE FROM director WHERE id = ?", id);
    }

    private RowMapper<List<Director>> mapperListAllDirectors() {
        return (rs, rowNum) -> {
            List<Director> directorList = new ArrayList<>();
            int check = -1;
            Director director = null;
            do {
                if (rs.getInt("director_id") != check) {
                    director = DirectorStorage.createDirectorBuilder(rs);
                    directorList.add(director);
                    check = rs.getInt("director_id");
                }
            } while (rs.next());
            return directorList;
        };
    }
}
