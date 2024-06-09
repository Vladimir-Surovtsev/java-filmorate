package ru.yandex.practicum.filmorate.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.FilmLike;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Primary
public class JdbcFilmRepository extends JdbcBaseRepository<Film> implements FilmRepository {
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final MpaRepository mpaRepository;
    private final FilmLikeRepository filmLikeRepository;
    private final FilmGenreRepository filmGenreRepository;

    private static final String FILMS_FIND_ALL_QUERY = """
            SELECT *
            FROM "films" AS f
            LEFT JOIN "mpas" AS r ON  f."mpa_id" = r."mpa_id";
            """;
    private static final String FILMS_INSERT_QUERY = """
            INSERT INTO "films" ("name" , "description" , "release_date" , "duration", "mpa_id")
                        VALUES (?, ?, ?, ?, ?);
            """;
    private static final String FILMS_UPDATE_QUERY = """
            UPDATE "films"
            SET "name" = ?,
                "description" = ?,
                "release_date" = ?,
                "duration" = ?,
                "mpa_id" = ?
            WHERE "film_id" = ?;
            """;
    private static final String FILMS_FIND_BY_ID_QUERY = """
            SELECT *
            FROM "films" AS f
            LEFT JOIN "mpas" AS r ON  f."mpa_id" = r."mpa_id"
            WHERE f."film_id" = ?;
            """;
    private static final String FILMS_ADD_LIKE_QUERY = """
            INSERT INTO "likes" ("film_id" , "user_id")
                        VALUES (?, ?);
            """;
    private static final String FILMS_DELETE_LIKE_QUERY = """
            DELETE FROM "likes"
            WHERE "film_id" = ?
                AND "user_id" = ?;
            """;
    private static final String FILMS_GET_POPULAR_QUERY = """
            SELECT
                f."film_id" AS "film_id",
                f."name" AS "name",
                f."description" AS "description",
                f."release_date" AS "release_date",
                f."duration" AS "duration",
                r."mpa_id" AS "mpa_id",
                r."mpa" AS "mpa",
            COUNT(l."film_id") AS count
            FROM "films" AS f
            LEFT JOIN "likes" AS l ON l."film_id" = f."film_id"
            LEFT JOIN "mpas" AS r ON  f."mpa_id" = r."mpa_id"
            GROUP BY f."film_id"
            ORDER BY count DESC
            LIMIT ?;
            """;
    private static final String FILMS_DELETE_FILMS_GENRE_QUERY = """
            DELETE FROM "films_genre"
            WHERE "film_id" = ?;
            """;
    private static final String FILMS_INSERT_FILMS_GENRE_QUERY = """
            INSERT INTO "films_genre" ("film_id", "genre_id")
                VALUES (?, ?);
            """;

    public JdbcFilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper, UserRepository userRepository,
                              GenreRepository genreRepository, MpaRepository mpaRepository,
                              FilmLikeRepository likeRepository, FilmGenreRepository filmGenreRepository) {
        super(jdbc, mapper);
        this.userRepository = userRepository;
        this.genreRepository = genreRepository;
        this.mpaRepository = mpaRepository;
        this.filmLikeRepository = likeRepository;
        this.filmGenreRepository = filmGenreRepository;
    }

    @Override
    public List<Film> findAll() {
        log.info("Получение списка фильмов");
        List<Film> films = findMany(FILMS_FIND_ALL_QUERY);
        setFilmsGenres(films);
        setFilmsLikes(films);
        return films;
    }

    @Override
    public Film findById(long id) {
        log.info("Получение фильма с id = {}", id);
        List<Film> films = findMany(FILMS_FIND_BY_ID_QUERY, id);
        if (films.size() != 1) {
            throw new NotFoundException("Фильм с id = " + id + " не найден!");
        }
        setFilmsGenres(films);
        setFilmsLikes(films);
        return films.iterator().next();
    }

    @Override
    public Film create(Film film) {
        validate(film);
        long id = insertGetKey(
                FILMS_INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(id);
        for (Genre genre : film.getGenres()) {
            insert(
                    FILMS_INSERT_FILMS_GENRE_QUERY,
                    film.getId(),
                    genre.getId()
            );
        }
        log.info("Фильм {} добавлен в список с id = {}", film.getName(), film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
        if (checkFilmExists(film.getId())) {
            validate(film);
            update(
                    FILMS_UPDATE_QUERY,
                    film.getName(),
                    film.getDescription(),
                    java.sql.Date.valueOf(film.getReleaseDate()),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId()
            );
            delete(
                    FILMS_DELETE_FILMS_GENRE_QUERY,
                    film.getId()
            );
            for (Genre genre : film.getGenres()) {
                insert(
                        FILMS_INSERT_FILMS_GENRE_QUERY,
                        film.getId(),
                        genre.getId()
                );
            }
            log.info("Фильм с id = {} обновлен", film.getId());
            return film;
        }
        throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
    }

    @Override
    public Film addLike(long id, long userId) {
        if (!checkFilmExists(id)) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
        if (!userRepository.checkUserExists(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        Film film = findOne(
                FILMS_FIND_BY_ID_QUERY,
                id
        ).orElse(null);
        insert(
                FILMS_ADD_LIKE_QUERY,
                id,
                userId
        );
        film.addLike(userId);
        log.info("Пользователь с id = {} поставил лайк фильму id = {}", userId, id);
        return film;
    }

    @Override
    public Film deleteLike(long id, long userId) {
        if (!checkFilmExists(id)) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
        if (!userRepository.checkUserExists(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }

        Film film = findOne(
                FILMS_FIND_BY_ID_QUERY,
                id
        ).orElse(null);
        delete(
                FILMS_DELETE_LIKE_QUERY,
                id,
                userId
        );
        film.deleteLike(userId);
        log.info("Пользователь с id = {} удалил лайк фильму id = {}", userId, id);
        return film;
    }

    @Override
    public List<Film> getPopular(long count) {
        if (count < 0) {
            throw new ValidationException("Параметр count должен быть больше 0");
        }
        log.info("Получение списка {} популярных фильмов", count);
        return findMany(
                FILMS_GET_POPULAR_QUERY,
                count
        );
    }

    public boolean checkFilmExists(long id) {
        return findOne(
                FILMS_FIND_BY_ID_QUERY,
                id).isPresent();
    }

    private void setFilmsGenres(List<Film> films) {
        String filmsId = films.stream()
                .map(film -> film.getId().toString())
                .collect(Collectors.joining(", "));
        LinkedHashSet<FilmGenre> filmGenres = filmGenreRepository.findGenresOfFilms(filmsId);
        for (Film film : films) {
            film.setGenres(filmGenres.stream()
                    .filter(filmGenre -> film.getId() == filmGenre.getFilmId())
                    .map(filmGenre -> new Genre(
                            filmGenre.getGenreId(),
                            filmGenre.getGenre())
                    )
                    .collect(Collectors.toSet()));
        }
    }

    private void setFilmsLikes(List<Film> films) {
        String filmsId = films.stream()
                .map(film -> film.getId().toString())
                .collect(Collectors.joining(", "));
        List<FilmLike> filmLikes = filmLikeRepository.findLikesOfFilms(filmsId);
        for (Film film : films) {
            film.setLikes(filmLikes.stream()
                    .filter(filmLike -> film.getId() == filmLike.getFilmId())
                    .map(FilmLike::getUserId)
                    .collect(Collectors.toSet()));
        }
    }

    private void validate(Film film) {
        genreRepository.checkGenresExists(film.getGenres());
        mpaRepository.checkMpaExists(film.getMpa().getId());
    }
}
