package ru.yandex.practicum.filmorate.storage.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;

@Repository
@Slf4j
@RequiredArgsConstructor
public class EventRepositoryImpl implements EventStorage {

    private final JdbcTemplate jdbcTemplate;
    private static final String EVENT_SQL = "INSERT INTO events " +
                                                      "(user_id, entity_id, event_type, operation, event_date) " +
                                                      "VALUES (?, ?, ?, ?, ?)";

    @Override
    public void addLikesEvent(Integer filmId, Integer userId, long timeStamp) {
        jdbcTemplate.update(EVENT_SQL, userId, filmId, "LIKE","ADD", new Date(timeStamp));
    }

    @Override
    public void deleteLikesEvent(Integer filmId, Integer userId, long timeStamp) {
        jdbcTemplate.update(EVENT_SQL, userId, filmId, "LIKE", "REMOVE", new Date(timeStamp));
    }

    @Override
    public void createReviewEvent(Integer reviewId, Integer userId, long timeStamp) {
        jdbcTemplate.update(EVENT_SQL, userId, reviewId, "REVIEW", "ADD", new Date(timeStamp));
    }

    @Override
    public void updateReviewEvent(Integer reviewId, Integer userId, long timeStamp) {
        jdbcTemplate.update(EVENT_SQL, userId, reviewId, "REVIEW", "UPDATE", new Date(timeStamp));
    }

    @Override
    public void deleteReviewEvent(Integer userId, Integer reviewId, long timeStamp) {
        jdbcTemplate.update(EVENT_SQL, userId, reviewId, "REVIEW", "REMOVE", new Date(timeStamp));
    }

    @Override
    public void addUserFriendEvent(Integer userId, Integer friendId, long timeStamp) {
        jdbcTemplate.update(EVENT_SQL, userId, friendId, "FRIEND", "ADD", new Date(timeStamp));
    }

    @Override
    public void deleteUserFriendEvent(Integer userId, Integer friendId, long timeStamp) {
        jdbcTemplate.update(EVENT_SQL, userId, friendId, "FRIEND", "REMOVE", new Date(timeStamp));
    }
}
