package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenreRepository {
    List<Genre> findAll();

    Genre findById(int id);

    boolean checkGenresExists(Set<Genre> genres);

    boolean checkGenreExists(int id);
}
