package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;


@RequiredArgsConstructor
@Service
public class UserService {

    private final UserStorage storage;

    public List<User> findAll() {
        return storage.findAll();
    }

    public User create(User user) {
        return storage.create(user);
    }

    public User update(User newUser) {
        return storage.update(newUser);
    }

    public User addToFriends(Long id, Long friendId) {
        return storage.addToFriends(id, friendId);
    }

    public User deleteFromFriends(Long id, Long friendId) {
        return storage.deleteFromFriends(id, friendId);
    }

    public List<User> findAllFriends(Long id) {
        return storage.findAllFriends(id);
    }

    public List<User> findCommonFriends(Long id, Long otherId) {
        return storage.findCommonFriends(id, otherId);
    }

}
