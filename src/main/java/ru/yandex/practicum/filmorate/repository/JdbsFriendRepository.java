package ru.yandex.practicum.filmorate.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friend;

import java.util.List;

@Slf4j
@Component
@Primary
public class JdbsFriendRepository extends JdbsBaseRepository<Friend> implements FriendRepository {
    private static final String FRIENDS_FIND_BY_USER_ID_QUERY = """
            SELECT *
            FROM "friends"
            WHERE "user_id" = ?;
            """;

    public JdbsFriendRepository(JdbcTemplate jdbc, RowMapper<Friend> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Friend> findFriendsOfUser(long id) {
        log.info("Получение списка друзей для пользователя с id = {}", id);
        return findMany(
                FRIENDS_FIND_BY_USER_ID_QUERY,
                id
        );
    }
}
