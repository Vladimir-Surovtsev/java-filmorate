package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final UserStorage userStorage;
    private long id = 0;

    private long generateId() {
        return ++id;
    }

    @Override
    public List<Film> findAll() {
        log.info("Получение списка фильмов");
        return new ArrayList<>(films.values());
    }

    @Override
    public Film create(Film film) {
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Фильм {} добавлен в список с id = {}", film.getName(), film.getId());
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        if (films.get(newFilm.getId()) == null) {
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }
        films.put(newFilm.getId(), newFilm);
        log.info("Фильм с id = {} обновлен", newFilm.getId());
        return newFilm;
    }

    @Override
    public Film addLike(Long id, Long userId) {
        if (!films.containsKey(id))
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        if (!userStorage.checkUserExists(userId))
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        films.get(id).addLike(userId);
        log.info("Пользователь с id = {} поставил лайк фильму c id = {}", userId, id);
        return films.get(id);
    }

    @Override
    public Film deleteLike(Long id, Long userId) {
        if (!films.containsKey(id))
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        if (!userStorage.checkUserExists(userId))
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        films.get(id).deleteLike(userId);
        log.info("Пользователь с id = {} удалил лайк фильму c id = {}", userId, id);
        return films.get(id);
    }

    @Override
    public List<Film> getPopular(Long count) {
        if (count <= 0) throw new ValidationException("Параметр count должен быть больше 0");
        log.info("Получение списка {} популярных фильмов", count);
        return films.values().stream()
                .sorted(Comparator.comparingInt(Film::getLikesCount).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

}
