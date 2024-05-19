package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class DirectorServiceLogic implements DirectorService {

    private final DirectorStorage dataDirectorStorage;

    @Override
    public List<Director> getDirectors() {
        log.trace("Получен запрос Get /directors");
        List<Director> directors = null;
        try {
            directors = dataDirectorStorage.getDirectors();
            return directors;
        } catch (EmptyResultDataAccessException e) {
            log.error("Пустой результат от базы данных. Возврат пустого списка.");
            return Collections.emptyList();
        }
    }

    @Override
    public Director getDirector(Integer id) {
        log.info("Получен запрос Get /directors - {}", id);
        return dataDirectorStorage.getDirector(id);
    }

    @Override
    public Director createDirector(Director director) {
        log.info("Получен запрос Post /directors - {}", director.getName());
        return dataDirectorStorage.createDirector(director);
    }

    @Override
    public Director updateDirector(Director director) {
        log.info("Получен запрос Put /directors - id {}, name {}", director.getId(), director.getName());
        return dataDirectorStorage.updateDirector(director);
    }

    @Override
    public void deleteDirector(Integer id) {
        log.debug("Получен запрос DELETE /directors/{} - удаление режиссера", id);
        dataDirectorStorage.deleteDirector(id);
    }
}
