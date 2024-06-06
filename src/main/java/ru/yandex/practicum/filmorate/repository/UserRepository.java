package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserRepository {
    List<User> findAll();

    User create(User user);

    User update(User newUser);

    User addToFriends(long id, long friendId);

    User deleteFromFriends(long id, long friendId);

    List<User> findAllFriends(long id);

    List<User> findCommonFriends(long id, long otherId);

    boolean checkUserExists(long id);
}
