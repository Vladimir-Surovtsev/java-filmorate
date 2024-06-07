package ru.yandex.practicum.filmorate.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Slf4j
@Component
@Primary
public class JdbcUserRepository extends JdbcBaseRepository<User> implements UserRepository {
    private static final int USERS_FRIENDSHIP_STATUS_CONFIRMED = 1;
    private static final int USERS_FRIENDSHIP_STATUS_UNCONFIRMED = 2;
    private static final String USERS_FIND_ALL_QUERY = """
            SELECT *
            FROM "users";
            """;
    private static final String USERS_INSERT_QUERY = """
            INSERT INTO "users" ("email", "login", "username", "birthday")
                        VALUES (?, ?, ?, ?);
            """;
    private static final String USERS_UPDATE_QUERY = """
            UPDATE "users"
            SET "email" = ?,
                "login" = ?,
                "username" = ?,
                "birthday" = ?
            WHERE "user_id" = ?;
            """;
    private static final String USERS_ADD_TO_FRIENDS_QUERY = """
            INSERT INTO "friends" ("user_id", "friend_id", "friendship_status_id")
            VALUES (?, ?, ?);
            """;
    private static final String USERS_DELETE_FROM_FRIENDS_QUERY = """
            DELETE FROM "friends"
            WHERE "user_id" = ?
                AND "friend_id" = ?;
            """;
    private static final String USERS_FIND_ALL_FRIENDS_QUERY = """
            SELECT *
            FROM "users" AS u
            WHERE "user_id" IN (
                SELECT "friend_id"
                FROM "friends"
                WHERE "user_id" = ?
                );
            """;
    private static final String USERS_FIND_COMMON_FRIENDS_QUERY = """
            SELECT *
            FROM "users" AS u
            WHERE u."user_id" IN (
                SELECT friends_of_first.friend
                FROM (
                    SELECT "friend_id" AS friend FROM "friends" WHERE "user_id" = ?
                    UNION
                    SELECT "user_id" AS friend FROM "friends" WHERE "friend_id" = ?
                    ) AS friends_of_first
                JOIN (
                    SELECT "friend_id" AS friend FROM "friends" WHERE "user_id" = ?
                    UNION
                    SELECT "user_id" AS friend FROM "friends" WHERE "friend_id" = ?
                    ) AS friends_of_second
                ON friends_of_first.friend = friends_of_second.friend
            );
            """;
    private static final String USERS_FIND_BY_ID_QUERY = """
            SELECT *
            FROM "users"
            WHERE "user_id" = ?;
            """;

    public JdbcUserRepository(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<User> findAll() {
        log.info("Получение списка пользователей");
        return findMany(USERS_FIND_ALL_QUERY);
    }

    @Override
    public User create(User user) {
        long id = insertGetKey(
                USERS_INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                java.sql.Date.valueOf(user.getBirthday())
        );
        user.setId(id);
        log.info("Пользователь {} добавлен в список с id = {}", user.getName(), user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        if (checkUserExists(user.getId())) {
            update(
                    USERS_UPDATE_QUERY,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    java.sql.Date.valueOf(user.getBirthday()),
                    user.getId()
            );
            log.info("Пользователь с id = {} обновлен", user.getId());
            return user;
        }
        throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
    }

    @Override
    public User addToFriends(long id, long friendId) {
        if (!checkUserExists(id))
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        if (!checkUserExists(friendId))
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        if (id == friendId)
            throw new ValidationException("Нельзя добавить самого себя в друзья (id = " + id + ")");
        User user = findOne(
                USERS_FIND_BY_ID_QUERY,
                id
        ).orElse(null);
        insert(
                USERS_ADD_TO_FRIENDS_QUERY,
                id,
                friendId,
                USERS_FRIENDSHIP_STATUS_UNCONFIRMED
        );
        user.addFriend(new Friend(friendId, USERS_FRIENDSHIP_STATUS_UNCONFIRMED));
        log.info("Пользователь с id = {} и пользователь с id = {} теперь друзья", friendId, id);
        return user;
    }

    @Override
    public User deleteFromFriends(long id, long friendId) {
        if (!checkUserExists(id))
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        if (!checkUserExists(friendId))
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        delete(
                USERS_DELETE_FROM_FRIENDS_QUERY,
                id,
                friendId
        );
        log.info("Пользователь с id = {} и пользователь с id = {} больше не друзья", friendId, id);
        return null;
    }

    @Override
    public List<User> findAllFriends(long id) {
        if (!checkUserExists(id))
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        log.info("Поиск друзей пользователя с id = {}", id);
        return findMany(
                USERS_FIND_ALL_FRIENDS_QUERY,
                id
        );
    }

    @Override
    public List<User> findCommonFriends(long id, long otherId) {
        if (!checkUserExists(id))
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        if (!checkUserExists(otherId))
            throw new NotFoundException("Пользователь с id = " + otherId + " не найден");
        log.info("Поиск общих друзей пользователя с id = {} и пользователя с id = {}", id, otherId);
        return findMany(
                USERS_FIND_COMMON_FRIENDS_QUERY,
                id,
                id,
                otherId,
                otherId
        );
    }

    @Override
    public boolean checkUserExists(long id) {
        return findOne(
                USERS_FIND_BY_ID_QUERY,
                id).isPresent();
    }
}
