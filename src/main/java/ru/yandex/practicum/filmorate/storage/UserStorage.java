package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> findAll();

    User create(User user);

    User update(User newUser);

    User addToFriends(Long id, Long friendId);

    User deleteFromFriends(Long id, Long friendId);

    List<User> findAllFriends(Long id);

    List<User> findCommonFriends(Long id, Long otherId);

    boolean checkUserExists(Long id);

}
