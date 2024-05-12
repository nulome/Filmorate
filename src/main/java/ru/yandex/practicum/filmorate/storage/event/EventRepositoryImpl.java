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
    private static final String ADD_LIKES_EVENT_SQL = "INSERT INTO events " +
                                                      "(user_id, entity_id, event_type, operation, event_date) " +
                                                      "VALUES (?, ?, 'LIKE', 'ADD', ?)";
    private static final String REMOVE_LIKES_EVENT_SQL = "INSERT INTO events " +
                                                         "(user_id, entity_id, event_type, operation, event_date) " +
                                                         "VALUES (?, ?, 'LIKE', 'REMOVE', ?)";
    private static final String CREATE_REVIEW_EVENT_SQL = "INSERT INTO events " +
                                                          "(user_id, entity_id, event_type, operation, event_date) " +
                                                          "VALUES (?, ?, 'REVIEW', 'ADD', ?)";
    private static final String UPDATE_REVIEW_EVENT_SQL = "INSERT INTO events " +
                                                          "(user_id, entity_id, event_type, operation, event_date) " +
                                                          "VALUES (?, ?, 'REVIEW', 'UPDATE', ?)";
    private static final String DELETE_REVIEW_EVENT_SQL = "INSERT INTO events " +
                                                          "(user_id, entity_id, event_type, operation, event_date) " +
                                                          "VALUES (?, ?, 'REVIEW', 'REMOVE', ?)";
    private static final String ADD_FRIEND_EVENT_SQL = "INSERT INTO events " +
                                                       "(user_id, entity_id, event_type, operation, event_date) " +
                                                       "VALUES (?, ?, 'FRIEND', 'ADD', ?)";
    private static final String DELETE_FRIEND_EVENT_SQL = "INSERT INTO events " +
                                                          "(user_id, entity_id, event_type, operation, event_date) " +
                                                          "VALUES (?, ?, 'FRIEND', 'REMOVE', ?)";

    @Override
    public void addLikesHandler(Integer filmId, Integer userId, long timeStamp) {
        jdbcTemplate.update(ADD_LIKES_EVENT_SQL, userId, filmId, new Date(timeStamp));
    }

    @Override
    public void deleteLikesHandler(Integer filmId, Integer userId, long timeStamp) {
        jdbcTemplate.update(REMOVE_LIKES_EVENT_SQL, userId, filmId, new Date(timeStamp));
    }

    @Override
    public void createReviewHandler(Integer reviewId, Integer userId, long timeStamp) {
        jdbcTemplate.update(CREATE_REVIEW_EVENT_SQL, userId, reviewId, new Date(timeStamp));
    }

    @Override
    public void updateReviewHandler(Integer reviewId, Integer userId, long timeStamp) {
        jdbcTemplate.update(UPDATE_REVIEW_EVENT_SQL, userId, reviewId, new Date(timeStamp));
    }

    @Override
    public void deleteReviewHandler(Integer userId, Integer reviewId, long timeStamp) {
        jdbcTemplate.update(DELETE_REVIEW_EVENT_SQL, userId, reviewId, new Date(timeStamp));
    }

    @Override
    public void addUserFriendHandler(Integer userId, Integer friendId, long timeStamp) {
        jdbcTemplate.update(ADD_FRIEND_EVENT_SQL, userId, friendId, new Date(timeStamp));
    }

    @Override
    public void deleteUserFriendHandler(Integer userId, Integer friendId, long timeStamp) {
        jdbcTemplate.update(DELETE_FRIEND_EVENT_SQL, userId, friendId, new Date(timeStamp));
    }
}
