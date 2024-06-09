package ru.yandex.practicum.filmorate.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.LinkedHashSet;

@Slf4j
@Component
@Primary
public class JdbcFilmGenreRepository extends JdbcBaseRepository<FilmGenre> implements FilmGenreRepository {
    private static final String GENRES_FIND_BY_FILM_ID_QUERY = """
            SELECT fg."film_id", g."genre_id", g."genre"
            FROM "films_genre" AS fg
            JOIN "genres" AS g ON fg."genre_id" = g."genre_id"
            WHERE "film_id" IN (%s);
            """;

    public JdbcFilmGenreRepository(JdbcTemplate jdbc, RowMapper<FilmGenre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public LinkedHashSet<FilmGenre> findGenresOfFilms(String filmsId) {
        return new LinkedHashSet<>(findMany(
                String.format(GENRES_FIND_BY_FILM_ID_QUERY, filmsId)
        ));
    }
}
