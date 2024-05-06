package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.related.UnknownValueException;
import ru.yandex.practicum.filmorate.related.ValidationException;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceLogic implements UserService {

    private final UserStorage dataUserStorage;

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
        return user.getFriendsList();
    }

    @Override
    public Set<Integer> deleteUserFriend(Integer userId, Integer friendId) {
        log.info("Получен запрос DELETE /users/{}/friends/{}", userId, friendId);
        User user = checkAndReceiptUserInDataBase(userId);
        checkAndReceiptUserInDataBase(friendId);
        user.getFriendsList().remove(friendId);
        dataUserStorage.updateUser(user);
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
}
