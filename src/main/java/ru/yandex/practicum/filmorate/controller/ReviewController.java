package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public Review createReview(@RequestBody @Valid Review review) {
        return reviewService.createReview(review);
    }

    @PutMapping
    public Review updateReview(@RequestBody @Valid Review review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{reviewId}")
    public boolean deleteReview(@PathVariable int reviewId) {
        return reviewService.deleteReview(reviewId);
    }

    @GetMapping("/{reviewId}")
    public Review getReview(@PathVariable int reviewId) {
        return reviewService.getReview(reviewId);
    }

    @GetMapping
    public List<Review> getAllReviews(@RequestParam(defaultValue = "0") int filmId,
                                      @RequestParam(defaultValue = "10") int count) {
        return reviewService.getAllReviews(filmId, count);
    }

    @PutMapping("/{reviewId}/like/{userId}")
    public Review addLikeReview(@PathVariable int reviewId, @PathVariable int userId) {
        return reviewService.addLikeReview(reviewId, userId);
    }

    @PutMapping("/{reviewId}/dislike/{userId}")
    public Review addDislikeReview(@PathVariable int reviewId, @PathVariable int userId) {
        return reviewService.addDislikeReview(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/like/{userId}")
    public Review removeLikeReview(@PathVariable int reviewId, @PathVariable int userId) {
        return reviewService.removeLikeReview(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/dislike/{userId}")
    public Review removeDislikeReview(@PathVariable int reviewId, @PathVariable int userId) {
        return reviewService.removeDislikeReview(reviewId, userId);
    }

}
