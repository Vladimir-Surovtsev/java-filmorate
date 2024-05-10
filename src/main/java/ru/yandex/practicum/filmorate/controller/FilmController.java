package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private long id = 0;

    private long generateId() {
        return ++id;
    }

    @GetMapping
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        film.setId(generateId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @Validated @RequestBody Film film) {
        if (films.get(film.getId()) == null) {
            log.error("Ошибка поиска фильма по идентификатору: {}", film.getId());
            throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
        }
        films.put(film.getId(), film);
        return film;
    }

}
