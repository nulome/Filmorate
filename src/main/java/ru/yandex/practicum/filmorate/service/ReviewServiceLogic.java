package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ReviewServiceLogic implements ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Override
    public Review createReview(Review review) {
        log.info("Получен запрос Post /reviews - {}", review.getId());
        userStorage.getUser(review.getUserId());
        filmStorage.getFilm(review.getFilmId());
        return reviewStorage.createReview(review);
    }

    @Override
    public Review updateReview(Review review) {
        log.info("Получен запрос Put /reviews/{}", review.getId());
        return reviewStorage.updateReview(review);
    }

    @Override
    public boolean deleteReview(int reviewId) {
        log.debug("Получен запрос DELETE /reviews/{}", reviewId);
        return reviewStorage.deleteReview(reviewId);
    }

    @Override
    public Review getReview(int reviewId) {
        log.info("Получен запрос GET /reviews/{}", reviewId);
        return reviewStorage.getReview(reviewId);
    }

    @Override
    public List<Review> getAllReviews(int filmId, int count) {
        log.info("Получен запрос GET /reviews с параметрами filmid = {} , count = {}", filmId, count);
        if (filmId == 0) {
            return reviewStorage.getAllReviews(count);
        } else return reviewStorage.getAllReviewsByFilmId(filmId, count);
    }

    @Override
    public Review addLikeReview(int reviewId, int userId) {
        log.info("Получен запрос Put /reviews/{}/like/{}", reviewId, userId);
        return reviewStorage.addLikeReview(reviewId, userId);
    }

    @Override
    public Review addDislikeReview(int reviewId, int userId) {
        log.info("Получен запрос Put /reviews/{}/dislike/{}", reviewId, userId);
        return reviewStorage.addDislikeReview(reviewId, userId);
    }

    @Override
    public Review removeLikeReview(int reviewId, int userId) {
        log.info("Получен запрос Delete /reviews/{}/like/{}", reviewId, userId);
        return reviewStorage.removeLikeReview(reviewId,userId);
    }

    @Override
    public Review removeDislikeReview(int reviewId, int userId) {
        log.info("Получен запрос Delete /reviews/{}/dislike/{}", reviewId, userId);
        return reviewStorage.removeDislikeReview(reviewId,userId);
    }
}
