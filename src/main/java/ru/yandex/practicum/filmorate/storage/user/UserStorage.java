package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public interface UserStorage {
    User createUser(User user);

    User updateUser(User user);

    List<User> getUsers();

    User getUser(Integer id);

    Map<Integer, Set<Integer>> getUsersLikes();
}
