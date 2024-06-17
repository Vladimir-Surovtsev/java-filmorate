package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.FilmLike;

import java.util.List;

public interface FilmLikeRepository {
    List<FilmLike> findLikesOfFilms(String filmsId);
}
