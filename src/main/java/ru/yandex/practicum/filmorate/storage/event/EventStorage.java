package ru.yandex.practicum.filmorate.storage.event;

public interface EventStorage {
    void addLikesEvent(Integer filmId, Integer userId, long timeStamp);

    void deleteLikesEvent(Integer filmId, Integer userId, long timeStamp);

    void createReviewEvent(Integer id, Integer userId, long timeStamp);

    void updateReviewEvent(Integer id, Integer userId, long timeStamp);

    void deleteReviewEvent(Integer userId, Integer reviewId, long timeStamp);

    void addUserFriendEvent(Integer userId, Integer friendId, long timeStamp);

    void deleteUserFriendEvent(Integer userId, Integer friendId, long timeStamp);
}
