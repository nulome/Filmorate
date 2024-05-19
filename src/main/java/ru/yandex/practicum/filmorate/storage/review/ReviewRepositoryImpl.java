package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;
    private static final String INSERT_REVIEW_SQL = "INSERT INTO reviews " +
            "(content, is_positive, film_id, user_id) " +
            "VALUES (?,?,?,?)";
    private static final String UPDATE_REVIEW_SQL = "UPDATE reviews " +
            "SET content = ?, is_positive = ? " +
            "WHERE id = ?";
    private static final String DELETE_REVIEW_SQL = "DELETE FROM reviews WHERE id = ?";
    private static final String FIND_REVIEW_BY_ID_SQL = "SELECT id, content, is_positive, " +
            "film_id, user_id, useful " +
            "FROM reviews " +
            "WHERE id = ? ";
    private static final String FIND_ALL_REVIEWS_SQL = "SELECT id, content, is_positive, " +
            "film_id, user_id, useful " +
            "FROM reviews " +
            "ORDER BY useful DESC " +
            "LIMIT ?";
    private static final String FIND_ALL_REVIEWS_BY_FILM_ID_SQL = "SELECT id, content, is_positive, " +
            "film_id, user_id, useful " +
            "FROM reviews " +
            "WHERE film_id = ? " +
            "ORDER BY useful DESC " +
            "LIMIT ?";
    private static final String ADD_LIKE_REMOVE_DISLIKE_SQL = "UPDATE reviews " +
            "SET useful = useful + 1 " +
            "WHERE id = ?";
    private static final String ADD_DISLIKE_REMOVE_LIKE_SQL = "UPDATE reviews " +
            "SET useful = useful - 1 " +
            "WHERE id = ?";

    @Override
    public Review createReview(Review review) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(INSERT_REVIEW_SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setInt(3, review.getFilmId());
            ps.setInt(4, review.getUserId());
            return ps;
        }, keyHolder);
        review.setId(keyHolder.getKey().intValue());
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        jdbcTemplate.update(UPDATE_REVIEW_SQL, review.getContent(), review.getIsPositive(), review.getId());
        return getReview(review.getId());
    }

    @Override
    public boolean deleteReview(int reviewId) {
        return jdbcTemplate.update(DELETE_REVIEW_SQL, reviewId) > 0;
    }

    @Override
    public Review getReview(int reviewId) {
        return jdbcTemplate.query(FIND_REVIEW_BY_ID_SQL, (rs, rowNum) -> Review.builder()
                        .id(rs.getInt(1))
                        .content(rs.getString(2))
                        .isPositive(rs.getBoolean(3))
                        .filmId(rs.getInt(4))
                        .userId(rs.getInt(5))
                        .useful(rs.getInt(6))
                        .build(), reviewId).stream()
                .findFirst().orElseThrow(() -> new EmptyResultDataAccessException(-1));
    }

    @Override
    public List<Review> getAllReviews(int count) {
        return jdbcTemplate.query(FIND_ALL_REVIEWS_SQL, (rs, rowNum) -> Review.builder()
                .id(rs.getInt(1))
                .content(rs.getString(2))
                .isPositive(rs.getBoolean(3))
                .filmId(rs.getInt(4))
                .userId(rs.getInt(5))
                .useful(rs.getInt(6))
                .build(), count);
    }

    @Override
    public List<Review> getAllReviewsByFilmId(int filmId, int count) {
        return jdbcTemplate.query(FIND_ALL_REVIEWS_BY_FILM_ID_SQL, (rs, rowNum) -> Review.builder()
                .id(rs.getInt(1))
                .content(rs.getString(2))
                .isPositive(rs.getBoolean(3))
                .filmId(rs.getInt(4))
                .userId(rs.getInt(5))
                .useful(rs.getInt(6))
                .build(), filmId, count);
    }

    @Override
    public Review addLikeReview(int reviewId, int userId) {
        jdbcTemplate.update(ADD_LIKE_REMOVE_DISLIKE_SQL, reviewId);
        return getReview(reviewId);
    }

    @Override
    public Review addDislikeReview(int reviewId, int userId) {
        jdbcTemplate.update(ADD_DISLIKE_REMOVE_LIKE_SQL, reviewId);
        return getReview(reviewId);
    }

    @Override
    public Review removeLikeReview(int reviewId, int userId) {
        jdbcTemplate.update(ADD_DISLIKE_REMOVE_LIKE_SQL, reviewId);
        return getReview(reviewId);
    }

    @Override
    public Review removeDislikeReview(int reviewId, int userId) {
        jdbcTemplate.update(ADD_LIKE_REMOVE_DISLIKE_SQL, reviewId);
        return getReview(reviewId);
    }
}
