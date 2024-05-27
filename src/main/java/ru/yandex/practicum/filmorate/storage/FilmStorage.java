package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> findAll();

    Film create(Film film);

    Film update(Film newFilm);

    Film addLike(Long id, Long userId);

    Film deleteLike(Long id, Long userId);

    List<Film> getPopular(Long count);

}
