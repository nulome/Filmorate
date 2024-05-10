package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
public interface UserStorage {
    User createUser(User user);

    User updateUser(User user);

    User deleteUser(Integer userId);

    List<User> getUsers();

    User getUser(Integer id);
}
