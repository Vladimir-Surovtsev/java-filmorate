package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.List;

public interface FilmGenreRepository {
    List<FilmGenre> findGenresOfFilms(String filmsId);
}
