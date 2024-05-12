package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.related.UnknownValueException;
import ru.yandex.practicum.filmorate.related.ValidationException;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceLogic implements UserService {

    private final UserStorage dataUserStorage;

    private final FilmStorage dataFilmStorage;

    private final EventStorage eventStorage;

    @Override
    public User createUser(User user) {
        log.info("Получен запрос Post /users - {}", user.getLogin());
        validation(user);
        return dataUserStorage.createUser(user);
    }

    @Override
    public User updateUser(User user) {
        log.info("Получен запрос Put /users - {}", user.getLogin());
        checkAndReceiptUserInDataBase(user.getId());
        validation(user);
        return dataUserStorage.updateUser(user);
    }

    @Override
    public User deleteUser(Integer userId) {
        log.info("Получен запрос Delete /users/{}", userId);
        checkAndReceiptUserInDataBase(userId);
        return dataUserStorage.deleteUser(userId);
    }

    @Override
    public List<User> getUsers() {
        log.trace("Получен запрос Get /users");
        return dataUserStorage.getUsers();
    }

    @Override
    public Set<Integer> addUserFriend(Integer userId, Integer friendId) {
        log.info("Получен запрос PUT /users/{}/friends/{}", userId, friendId);
        User user = checkAndReceiptUserInDataBase(userId);
        checkAndReceiptUserInDataBase(friendId);
        user.getFriendsList().add(friendId);
        dataUserStorage.updateUser(user);
        eventStorage.addUserFriendHandler(userId, friendId, System.currentTimeMillis());
        return user.getFriendsList();
    }

    @Override
    public Set<Integer> deleteUserFriend(Integer userId, Integer friendId) {
        log.info("Получен запрос DELETE /users/{}/friends/{}", userId, friendId);
        User user = checkAndReceiptUserInDataBase(userId);
        checkAndReceiptUserInDataBase(friendId);
        user.getFriendsList().remove(friendId);
        dataUserStorage.updateUser(user);
        eventStorage.deleteUserFriendHandler(userId, friendId, System.currentTimeMillis());
        return user.getFriendsList();
    }

    @Override
    public List<User> getFriendsList(Integer userId) {
        log.trace("Получен запрос GET /users/{}/friends", userId);
        List<User> users = new ArrayList<>();
        for (Integer id : dataUserStorage.getUser(userId).getFriendsList()) {
            users.add(checkAndReceiptUserInDataBase(id));
        }
        return users;
    }

    @Override
    public List<User> getCommonFriend(Integer userId, Integer friendId) {
        log.trace("Получен запрос GET /users/{}/friends/common/{}", userId, friendId);
        User user = checkAndReceiptUserInDataBase(userId);
        User friend = checkAndReceiptUserInDataBase(friendId);
        List<User> users = new ArrayList<>();
        for (Integer idList : user.getFriendsList()) {
            if (friend.getFriendsList().contains(idList)) {
                users.add(checkAndReceiptUserInDataBase(idList));
            }
        }
        return users;
    }

    @Override
    public User getUser(Integer id) {
        log.trace("Получен запрос GET /users/{}", id);
        return checkAndReceiptUserInDataBase(id);
    }

    @Override
    public List<Film> getRecommendations(Integer userId) {
        log.trace("Получен запрос GET /users/{}/recommendations", userId);
        Map<Integer, Set<Integer>> likesByUser = dataUserStorage.getUsersLikes();
        checkAndReceiptUserInDataBase(userId);
        if (!likesByUser.containsKey(userId)) {
            return new ArrayList<>();
        }
        Map<Integer, Integer> likedFilmIntersections = getLikedFilmIntersections(likesByUser, userId);
        if (likedFilmIntersections.isEmpty()) {
            return new ArrayList<>();
        }
        int maxIntersectionUser = Collections.max(likedFilmIntersections.entrySet(), Map.Entry.comparingByValue()).getKey();

        Set<Integer> maxUserIds = likedFilmIntersections.entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(), likedFilmIntersections.get(maxIntersectionUser)))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        Set<Integer> recommendationsIds = maxUserIds.stream()
                .map(likesByUser::get)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());

        recommendationsIds.removeAll(likesByUser.get(userId));

        return dataFilmStorage.getFilms()
                .stream()
                .filter(o -> recommendationsIds.contains(o.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> getUserFeed(int userId) {
        checkAndReceiptUserInDataBase(userId);
        log.trace("Получен запрос GET /users/{}/feed", userId);
        return dataUserStorage.getUserFeed(userId);
    }

    private void validation(User user) {
        checkNameNotEmpty(user);
        checkListFriendsNotNull(user);
        if (user.getBirthday().isAfter(LocalDate.now())) {
            String message = "Дата рождения не может быть в будущем: " + user.getBirthday().toString();
            log.warn(message);
            throw new ValidationException(message);
        }
    }

    private void checkNameNotEmpty(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private void checkListFriendsNotNull(User user) {
        if (user.getFriendsList() == null) {
            user.setFriendsList(new HashSet<>());
        }
    }

    private User checkAndReceiptUserInDataBase(Integer id) {
        try {
            return dataUserStorage.getUser(id);
        } catch (EmptyResultDataAccessException e) {
            log.error("Ошибка в запросе к базе данных. Не найдено значение по id: {} \n {}", id, e.getMessage());
            throw new UnknownValueException("Передан не верный id: " + id);
        }
    }

    private Map<Integer, Integer> getLikedFilmIntersections(Map<Integer, Set<Integer>> likes, Integer keyUser) {
        Map<Integer, Integer> intersections = new HashMap<>();
        for (Integer userId : likes.keySet()) {
            if (!userId.equals(keyUser)) {
                Set<Integer> intersection = new HashSet<>(likes.get(keyUser));
                intersection.retainAll(likes.get(userId));
                if (intersection.size() > 0) {
                    intersections.put(userId, intersection.size());
                }
            }
        }
        return intersections;
    }
}
