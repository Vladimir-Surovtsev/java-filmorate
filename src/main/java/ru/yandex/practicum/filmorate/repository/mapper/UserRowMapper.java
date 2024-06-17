package ru.yandex.practicum.filmorate.repository.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FriendRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class UserRowMapper implements RowMapper<User> {
    private final FriendRepository friendRepository;

    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("user_id"));
        user.setName(resultSet.getString("username"));
        user.setEmail(resultSet.getString("email"));
        user.setLogin(resultSet.getString("login"));
        user.setBirthday(resultSet.getDate("birthday").toLocalDate());
        user.setFriends(new HashSet<>(friendRepository.findFriendsOfUser(user.getId())));
        return user;
    }
}
