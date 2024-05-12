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
    public void addLikesHandler(Integer filmId, Integer userId, long timeStamp) {
        jdbcTemplate.update(EVENT_SQL, userId, filmId, "LIKE","ADD", new Date(timeStamp));
    }

    @Override
    public void deleteLikesHandler(Integer filmId, Integer userId, long timeStamp) {
        jdbcTemplate.update(EVENT_SQL, userId, filmId, "LIKE", "REMOVE", new Date(timeStamp));
    }

    @Override
    public void createReviewHandler(Integer reviewId, Integer userId, long timeStamp) {
        jdbcTemplate.update(EVENT_SQL, userId, reviewId, "REVIEW", "ADD", new Date(timeStamp));
    }

    @Override
    public void updateReviewHandler(Integer reviewId, Integer userId, long timeStamp) {
        jdbcTemplate.update(EVENT_SQL, userId, reviewId, "REVIEW", "UPDATE", new Date(timeStamp));
    }

    @Override
    public void deleteReviewHandler(Integer userId, Integer reviewId, long timeStamp) {
        jdbcTemplate.update(EVENT_SQL, userId, reviewId, "REVIEW", "REMOVE", new Date(timeStamp));
    }

    @Override
    public void addUserFriendHandler(Integer userId, Integer friendId, long timeStamp) {
        jdbcTemplate.update(EVENT_SQL, userId, friendId, "FRIEND", "ADD", new Date(timeStamp));
    }

    @Override
    public void deleteUserFriendHandler(Integer userId, Integer friendId, long timeStamp) {
        jdbcTemplate.update(EVENT_SQL, userId, friendId, "FRIEND", "REMOVE", new Date(timeStamp));
    }
}
