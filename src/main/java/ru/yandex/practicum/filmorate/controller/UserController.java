package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    @GetMapping
    public List<User> findAll() {
        return service.findAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return service.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        return service.update(newUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addToFriends(@PathVariable long id,
                             @PathVariable long friendId) {
        return service.addToFriends(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFromFriends(@PathVariable long id,
                                  @PathVariable long friendId) {
        service.deleteFromFriends(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> findAllFriends(@PathVariable long id) {
        return service.findAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findCommonFriends(@PathVariable long id,
                                        @PathVariable long otherId) {
        return service.findCommonFriends(id, otherId);
    }
}
