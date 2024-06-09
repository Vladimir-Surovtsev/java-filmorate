package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.LinkedHashSet;

public interface FilmGenreRepository {
    LinkedHashSet<FilmGenre> findGenresOfFilms(String filmsId);
}
