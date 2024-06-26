package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.related.EventType;
import ru.yandex.practicum.filmorate.related.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_ALL_USERS_SQL = "SELECT u.id, u.login, u.name, u.email, u.birthday, f.friend_id" +
            " FROM users u LEFT JOIN friends f ON u.id = f.user_id ORDER BY u.id";

    private static final String SELECT_USER_BY_ID_SQL = "SELECT u.id, u.login, u.name, u.email, u.birthday, f.friend_id" +
            " FROM users u LEFT JOIN friends f ON u.id = f.user_id WHERE u.id = ?";

    private static final String UPDATE_USER_SQL = "UPDATE users SET login = ?, name  = ?, email  = ?, birthday  = ? " +
            "WHERE id = ?";

    private static final String DELETE_USER_SQL = "DELETE FROM users WHERE id = ?";

    private static final String SELECT_USER_FEED_SQL = "SELECT id, user_id, entity_id, event_type, operation, event_date " +
            "FROM events WHERE user_id = ?";

    @Override
    public User createUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        Map<String, String> params = Map.of("login", user.getLogin(), "name", user.getName(),
                "email", user.getEmail(), "birthday", user.getBirthday().toString());
        Number id = simpleJdbcInsert.executeAndReturnKey(params);
        user.setId(id.intValue());

        updFriendsListInDataBase(user);
        return getUser(user.getId());
    }

    @Override
    public User updateUser(User user) {
        jdbcTemplate.update(UPDATE_USER_SQL, user.getLogin(), user.getName(), user.getEmail(),
                user.getBirthday(), user.getId());
        updFriendsListInDataBase(user);
        return getUser(user.getId());
    }

    @Override
    public User deleteUser(Integer userId) {
        User user = getUser(userId);
        jdbcTemplate.update(DELETE_USER_SQL, userId);
        return user;
    }

    @Override
    public List<User> getUsers() {
        return jdbcTemplate.queryForObject(SELECT_ALL_USERS_SQL, mapperListAllUser());
    }


    @Override
    public User getUser(Integer id) {
        return jdbcTemplate.queryForObject(SELECT_USER_BY_ID_SQL, (rs, rowNum) -> {
            User user = createUserBuilder(rs);
            do {
                addListFriendsInUser(rs, user);
            } while (rs.next());

            return user;
        }, id);
    }

    public Map<Integer, Set<Integer>> getUsersLikes() {
        return jdbcTemplate.query("SELECT l.user_id, l.film_id FROM likes l ORDER BY l.user_id", this::mapperListAllLikes);
    }

    @Override
    public List<Event> getUserFeed(int userId) {
        return jdbcTemplate.query(SELECT_USER_FEED_SQL, (rs, rowNum) -> Event.builder()
                .id(rs.getInt(1))
                .userId(rs.getInt(2))
                .entityId(rs.getInt(3))
                .eventType(EventType.valueOf(rs.getString(4)))
                .operation(Operation.valueOf(rs.getString(5)))
                .eventDate(rs.getDate(6).getTime())
                .build(), userId);
    }

    private Map<Integer, Set<Integer>> mapperListAllLikes(ResultSet rs) throws SQLException {
        Map<Integer, Set<Integer>> likes = new HashMap<>();
        while (rs.next()) {
            Integer userId = rs.getInt("user_id");
            Integer filmId = rs.getInt("film_id");
            if (!likes.containsKey(userId)) {
                likes.put(userId, new HashSet<>());
            }
            likes.get(userId).add(filmId);
        }
        return likes;
    }


    private RowMapper<List<User>> mapperListAllUser() {
        return (rs, rowNum) -> {
            List<User> usersList = new ArrayList<>();
            int check = -1;
            User user = null;
            do {
                if (rs.getInt("id") != check) {
                    user = createUserBuilder(rs);
                    addListFriendsInUser(rs, user);
                    usersList.add(user);
                    check = rs.getInt("id");
                } else {
                    addListFriendsInUser(rs, user);
                }
            } while (rs.next());
            return usersList;
        };
    }

    private void addListFriendsInUser(ResultSet rs, User user) throws SQLException {
        if (rs.getInt("friend_id") != 0) {
            user.getFriendsList().add(rs.getInt("friend_id"));
        }
    }

    private void updFriendsListInDataBase(User user) {
        jdbcTemplate.update("DELETE FROM friends WHERE user_id = ?", user.getId());
        if (!user.getFriendsList().isEmpty()) {
            for (Integer friend : user.getFriendsList()) {
                jdbcTemplate.update("INSERT INTO friends (user_id, friend_id) VALUES (?, ?)", user.getId(), friend);
            }
        }
    }

    private User createUserBuilder(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getInt("id"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .friendsList(new HashSet<>())
                .build();
    }

}
