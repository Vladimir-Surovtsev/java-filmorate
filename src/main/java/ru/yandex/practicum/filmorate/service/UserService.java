package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository repository;

    public List<User> findAll() {
        return repository.findAll();
    }

    public User create(User user) {
        return repository.create(user);
    }

    public User update(User newUser) {
        return repository.update(newUser);
    }

    public User addToFriends(long id, long friendId) {
        return repository.addToFriends(id, friendId);
    }

    public User deleteFromFriends(long id, long friendId) {
        return repository.deleteFromFriends(id, friendId);
    }

    public List<User> findAllFriends(long id) {
        return repository.findAllFriends(id);
    }

    public List<User> findCommonFriends(long id, Long otherId) {
        return repository.findCommonFriends(id, otherId);
    }
}
