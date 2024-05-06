# java-filmorate
Template repository for Filmorate project.

![Screenshot dbdiagram.](/src/main/resources/dbdiagram.png)

Основные таблицы Юзеры и Фильмы. Users/films.
Друзья хранятся в записях согласия/подтверждения дружбы  (1:2, 1:3, 2:1 и т.д.). Friends.
Поставленные лайки хранятся по фильму (1:1, 1:2, 1:3, 5:1 и т.д.). Likes.
Рейтинги (mpa) и жанры (genre) к фильмам вынесены в отдельные таблицы.
Соотношения фильмов к таблицам рейтингов и фильмов хранятся в смежных таблицах соотношения. Film_mpa/film_genres.


Основной запрос ко всем фильмам:
SELECT f.id, f.name, f.description, f.releasedate, f.duration,
l.user_id AS likes, fg.genre_id, g.name AS genre_name, fm.mpa_id, m.name AS mpa_name
FROM films f
LEFT JOIN film_genres fg ON f.id = fg.film_id
LEFT JOIN genre g ON fg.genre_id = g.id
LEFT JOIN film_mpa fm ON f.id = fm.film_id
LEFT JOIN mpa m ON fm.mpa_id = m.id
LEFT JOIN likes l ON f.id = l.film_id ORDER BY f.id;

Основной запрос ко всем пользователям:
SELECT u.id, u.login, u.name, u.email, u.birthday, f.friend_id
FROM users u
LEFT JOIN friends f ON u.id = f.user_id
ORDER BY u.id;
