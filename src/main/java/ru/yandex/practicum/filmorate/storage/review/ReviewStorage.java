package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review createReview(Review review);

    Review updateReview(Review review);

    boolean deleteReview(int reviewId);

    Review getReview(int reviewId);

    List<Review> getAllReviews(int count);

    List<Review> getAllReviewsByFilmId(int filmId, int count);

    Review addLikeReview(int reviewId, int userId);

    Review addDislikeReview(int reviewId, int userId);

    Review removeLikeReview(int reviewId, int userId);

    Review removeDislikeReview(int reviewId, int userId);

}
