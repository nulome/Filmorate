package ru.yandex.practicum.filmorate.storage.event;

public interface EventStorage {
    void addLikesHandler(Integer filmId, Integer userId, long timeStamp);

    void deleteLikesHandler(Integer filmId, Integer userId, long timeStamp);

    void createReviewHandler(Integer id, Integer userId, long timeStamp);

    void updateReviewHandler(Integer id, Integer userId, long timeStamp);

    void deleteReviewHandler(Integer userId, Integer reviewId, long timeStamp);

    void addUserFriendHandler(Integer userId, Integer friendId, long timeStamp);

    void deleteUserFriendHandler(Integer userId, Integer friendId, long timeStamp);
}
