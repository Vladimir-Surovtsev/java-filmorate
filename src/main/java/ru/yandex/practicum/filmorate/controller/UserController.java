package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Validated
@Slf4j
public class UserController {
    private long id = 0;

    private final Map<Long, User> users = new HashMap<>();

    private long generateId() {
        return ++id;
    }

    @GetMapping
    public List<User> getAll() {
        log.info("GET /users ==> {}", users);
        return new ArrayList<>(users.values());
    }

    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) {
        log.info("Create User: {} - Started", user);
        user.setId(generateId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Create User: {} - Finished", user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        if (users.get(user.getId()) == null) {
            log.error("Ошибка поиска пользователя по id: {}", user.getId());
            throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
        }
        users.put(user.getId(), user);
        return user;
    }
}
