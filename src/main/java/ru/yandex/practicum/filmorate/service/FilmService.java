package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmRepository repository;

    public List<Film> findAll() {
        return repository.findAll();
    }

    public Film create(Film film) {
        return repository.create(film);
    }

    public Film update(Film newFilm) {
        return repository.update(newFilm);
    }

    public Film addLike(long id, long userId) {
        return repository.addLike(id, userId);
    }

    public Film deleteLike(long id, long userId) {
        return repository.deleteLike(id, userId);
    }

    public List<Film> getPopular(long count) {
        return repository.getPopular(count);
    }

    public Film findById(long id) {
        return repository.findById(id);
    }
}
