package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmRepository {
    List<Film> findAll();

    Film findById(long id);

    Film create(Film film);

    Film update(Film newFilm);

    Film addLike(long id, long userId);

    Film deleteLike(long id, long userId);

    List<Film> getPopular(long count);
}
