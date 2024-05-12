package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.related.ValidationException;
import ru.yandex.practicum.filmorate.service.UserServiceLogic;
import ru.yandex.practicum.filmorate.storage.event.EventRepositoryImpl;
import ru.yandex.practicum.filmorate.storage.film.FilmRepositoryImpl;
import ru.yandex.practicum.filmorate.storage.user.UserRepositoryImpl;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
class UserControllerTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    UserController userController;
    Validator validator;

    @BeforeEach
    void create() {
        userController = new UserController(new UserServiceLogic(new UserRepositoryImpl(jdbcTemplate), new FilmRepositoryImpl(jdbcTemplate), new EventRepositoryImpl(jdbcTemplate)));

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @Sql({"/schema.sql"})
    void checkTestUserController() throws ValidationException {
        User checkUser = createUser("Nikolai", "d1nf@jhdsf");
        userController.createUser(checkUser);
        assertEquals(1, checkUser.getId(), "Не записывается id");

        User checkUser1 = createUser("Jul", "d2df@jh");
        userController.createUser(checkUser1);
        assertEquals(2, checkUser1.getId(), "Не обновляется id");

        User check = createUser("Gerberd", "d234df@jh");
        check.setName("");
        checkUser = userController.createUser(check);
        assertEquals(check.getLogin(), checkUser.getName(), "Не обновляется пустое значение имени");

        List<User> list = userController.getUsers();
        assertEquals(3, list.size(), "Не корректно возвращает список пользователей");
    }

    @Test
    @Sql({"/schema.sql"})
    @Sql({"/data.sql"})
    void checkValidationMethodUserController() throws ValidationException {
        User checkUser = null;
        User user = createUser("Chil", "d245f@jh");
        user.setBirthday(LocalDate.of(3500, 1, 1));
        ValidationException exceptionDateWarn = assertThrows(ValidationException.class, () -> {
            userController.createUser(user);
        }, "Дата рождения в будущем. Ожидалось исключение ValidationException.");

        user.setBirthday(LocalDate.of(2000, 1, 1));
        user.setLogin("  ");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Login не должен быть пустым");

        user.setLogin("log in");
        violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Login не должен включать пробел");

        user.setLogin("login");
        user.setEmail("email");
        violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "email должен включать спец. символ");

        user.setEmail("  ");
        violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "email не может быть пустым");

    }

    private User createUser(String str, String email) {
        return User.builder()
                .login(str)
                .name("userName")
                .email(email)
                .birthday(LocalDate.of(2000, 1, 1))
                .friendsList(new HashSet<>())
                .build();
    }

}