package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface DirectorStorage {
    List<Director> getDirectors();

    Director getDirector(Integer id);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(Integer id);

    static Director createDirectorBuilder(ResultSet rs) throws SQLException {
        return Director.builder()
                .id(rs.getInt("director_id"))
                .name(rs.getString("director_name"))
                .build();
    }
}
