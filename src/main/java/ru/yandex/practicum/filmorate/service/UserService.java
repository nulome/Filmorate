package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    User createUser(User user);

    User updateUser(User user);

    User deleteUser(Integer userId);

    List<User> getUsers();

    Set<Integer> addUserFriend(Integer userId, Integer friendId);

    Set<Integer> deleteUserFriend(Integer userId, Integer friendId);

    List<User> getFriendsList(Integer userId);

    List<User> getCommonFriend(Integer userId, Integer friendId);

    User getUser(Integer id);

    List<Film> getRecommendations(Integer userId);

    List<Event> getUserFeed(int userId);
}
