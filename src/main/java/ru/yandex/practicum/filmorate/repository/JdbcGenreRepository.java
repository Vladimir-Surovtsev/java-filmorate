package ru.yandex.practicum.filmorate.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
@Primary
public class JdbcGenreRepository extends JdbcBaseRepository<Genre> implements GenreRepository {
    private static final String GENRES_FIND_ALL_QUERY = """
            SELECT *
            FROM "genres";
            """;
    private static final String GENRES_FIND_BY_ID_QUERY = """
            SELECT *
            FROM "genres"
            WHERE "genre_id" = ?;
            """;

    public JdbcGenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Genre> findAll() {
        log.info("Получение списка жанров");
        return findMany(GENRES_FIND_ALL_QUERY);
    }

    @Override
    public Genre findById(int id) {
        log.info("Получение жанра с id = {}", id);
        return findOne(
                GENRES_FIND_BY_ID_QUERY,
                id
        ).orElseThrow(() -> new NotFoundException("Жанр с id = " + id + " не найден!"));
    }

    public boolean checkGenresExists(Set<Genre> genres) {
        for (Genre genre : genres) {
            if (!checkGenreExists(genre.getId()))
                throw new ParameterNotValidException("Жанр с id = " + genre.getId(), " не найден!");
        }
        return true;
    }

    public boolean checkGenreExists(int id) {
        return findOne(
                GENRES_FIND_BY_ID_QUERY,
                id).isPresent();
    }
}
