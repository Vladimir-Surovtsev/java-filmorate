package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;


@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmStorage storage;

    public List<Film> findAll() {
        return storage.findAll();
    }

    public Film create(Film film) {
        return storage.create(film);
    }

    public Film update(Film newFilm) {
        return storage.update(newFilm);
    }

    public Film addLike(Long id, Long userId) {
        return storage.addLike(id, userId);
    }

    public Film deleteLike(Long id, Long userId) {
        return storage.deleteLike(id, userId);
    }

    public List<Film> getPopular(Long count) {
        return storage.getPopular(count);
    }

}