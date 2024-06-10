package ru.yandex.practicum.filmorate.repository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = {JdbcFilmRepository.class,
        JdbcGenreRepository.class,
        JdbcUserRepository.class,
        JdbcMpaRepository.class,
        JdbcFilmLikeRepository.class,
        JdbcFriendRepository.class,
        JdbcFilmGenreRepository.class})
@ComponentScan(basePackages = {"ru.yandex.practicum.filmorate.repository.mapper"})
class JdbcFilmRepositoryTest {
    @Autowired
    private final JdbcFilmRepository jdbcFilmRepository;
    @Autowired
    private final JdbcUserRepository jdbcUserRepository;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @AllArgsConstructor
    static class ExpectedViolation {
        String propertyPath;
        String message;
    }

    public Film getTestFilm(int id) {
        return switch (id) {
            case 1 -> Film.builder()
                    .id(null)
                    .name("Фильм №1")
                    .description("Описание фильма №1")
                    .releaseDate(LocalDate.now())
                    .duration(90)
                    .mpa(new Mpa(1, "G"))
                    .genres(Set.of(
                            new Genre(1, "Комедия"),
                            new Genre(2, "Драма")))
                    .build();
            case 2 -> Film.builder()
                    .id(null)
                    .name("Фильм №2")
                    .description("Описание фильма №2")
                    .releaseDate(LocalDate.now().minusYears(1))
                    .duration(90)
                    .mpa(new Mpa(2, "PG"))
                    .genres(Set.of(
                            new Genre(3, "Мультфильм")))
                    .build();
            case 3 -> Film.builder()
                    .id(null)
                    .name("Фильм №3")
                    .description("Описание фильма №3")
                    .releaseDate(LocalDate.now().minusMonths(3))
                    .duration(50)
                    .mpa(new Mpa(3, "PG-13"))
                    .genres(Set.of(
                            new Genre(4, "Триллер")))
                    .build();
            default -> null;
        };
    }

    public User getTestUser(int id) {
        return switch (id) {
            case 1 -> User.builder()
                    .name("User 1")
                    .email("user@ya.ru")
                    .login("userLogin1")
                    .birthday(LocalDate.of(2000, 2, 20))
                    .build();
            case 2 -> User.builder()
                    .name("User 2")
                    .email("user2@ya.ru")
                    .login("userLogin2")
                    .birthday(LocalDate.of(2001, 2, 20))
                    .build();
            case 3 -> User.builder()
                    .name("User 3")
                    .email("user3@ya.ru")
                    .login("userLogin3")
                    .birthday(LocalDate.of(2002, 2, 20))
                    .build();
            default -> null;
        };
    }

    @Test
    void findAll() {
        Film film = getTestFilm(1);
        jdbcFilmRepository.create(film);
        Film film2 = getTestFilm(2);
        jdbcFilmRepository.create(film2);

        List<Film> responseEntity = jdbcFilmRepository.findAll();
        assertNotNull(responseEntity);
        assertEquals(2, responseEntity.size());
    }

    @Test
    void create() {
        Film film = getTestFilm(1);
        jdbcFilmRepository.create(film);
        List<Film> responseEntity = jdbcFilmRepository.findAll();
        assertNotNull(responseEntity);
        assertEquals(1, responseEntity.size());
        assertNotNull(responseEntity.iterator().next().getId());
        assertEquals(film.getName(), responseEntity.iterator().next().getName());
        assertEquals(film.getDescription(), responseEntity.iterator().next().getDescription());
        assertEquals(film.getReleaseDate(), responseEntity.iterator().next().getReleaseDate());
        assertEquals(film.getDuration(), responseEntity.iterator().next().getDuration());
    }

    @Test
    void update() {
        Film film = getTestFilm(1);
        jdbcFilmRepository.create(film);
        List<Film> responseEntity = jdbcFilmRepository.findAll();
        final Film newFilm = getTestFilm(2);
        newFilm.setId(responseEntity.iterator().next().getId());
        jdbcFilmRepository.update(newFilm);
        responseEntity = jdbcFilmRepository.findAll();

        assertNotNull(responseEntity);
        assertEquals(1, responseEntity.size());
        assertEquals(newFilm.getName(), responseEntity.iterator().next().getName());
        assertEquals(newFilm.getDescription(), responseEntity.iterator().next().getDescription());
        assertEquals(newFilm.getReleaseDate(), responseEntity.iterator().next().getReleaseDate());
        assertEquals(newFilm.getDuration(), responseEntity.iterator().next().getDuration());
    }

    @Test
    void createNullNameFilm() {
        Film film = getTestFilm(1);
        film.setName(null);
        List<ConstraintViolation<Film>> violations = new ArrayList<>(validator.validate(film));

        ExpectedViolation expectedViolation = new ExpectedViolation(
                "name", "must not be blank");
        assertEquals(1, violations.size());
        assertEquals(
                expectedViolation.propertyPath,
                violations.getFirst().getPropertyPath().toString()
        );
        assertEquals(
                expectedViolation.message,
                violations.getFirst().getMessage()
        );
    }

    @Test
    void createDescriptionMore200() {
        Film film = getTestFilm(1);
        film.setDescription("description".repeat(20));
        List<ConstraintViolation<Film>> violations = new ArrayList<>(validator.validate(film));

        ExpectedViolation expectedViolation = new ExpectedViolation(
                "description", "size must be between 0 and 200");
        assertEquals(1, violations.size());
        assertEquals(
                expectedViolation.propertyPath,
                violations.getFirst().getPropertyPath().toString()
        );
        assertEquals(
                expectedViolation.message,
                violations.getFirst().getMessage()
        );
    }

    @Test
    void addLike() {
        Film film = getTestFilm(1);
        long filmId = jdbcFilmRepository.create(film).getId();

        User user = getTestUser(1);
        long user1Id = jdbcUserRepository.create(user).getId();

        jdbcFilmRepository.addLike(filmId, user1Id);

        ArrayList<Film> responseEntity = new ArrayList<>(jdbcFilmRepository.findAll());
        assertNotNull(responseEntity);
        assertEquals(1, responseEntity.size());
        assertEquals(1, responseEntity.getFirst().getLikesCount());
        assertTrue(responseEntity.getFirst().getLikes().contains(user1Id));
    }

    @Test
    void deleteLike() {
        Film film = getTestFilm(1);
        long filmId = jdbcFilmRepository.create(film).getId();

        User user = getTestUser(1);
        long user1Id = jdbcUserRepository.create(user).getId();

        jdbcFilmRepository.addLike(filmId, user1Id);
        jdbcFilmRepository.deleteLike(filmId, user1Id);

        ArrayList<Film> responseEntity = new ArrayList<>(jdbcFilmRepository.findAll());
        assertNotNull(responseEntity);
        assertEquals(1, responseEntity.size());
        assertEquals(0, responseEntity.getFirst().getLikesCount());
    }

    @Test
    void getPopularCount10() {
        Film film1 = getTestFilm(1);
        long film1Id = jdbcFilmRepository.create(film1).getId();
        Film film2 = getTestFilm(2);
        long film2Id = jdbcFilmRepository.create(film2).getId();
        Film film3 = getTestFilm(3);
        long film3Id = jdbcFilmRepository.create(film3).getId();

        User user1 = getTestUser(1);
        long user1Id = jdbcUserRepository.create(user1).getId();
        User user2 = getTestUser(2);
        long user2Id = jdbcUserRepository.create(user2).getId();
        User user3 = getTestUser(3);
        long user3Id = jdbcUserRepository.create(user3).getId();

        jdbcFilmRepository.addLike(film2Id, user1Id);
        jdbcFilmRepository.addLike(film2Id, user2Id);
        jdbcFilmRepository.addLike(film2Id, user3Id);

        jdbcFilmRepository.addLike(film3Id, user1Id);
        jdbcFilmRepository.addLike(film3Id, user2Id);

        ArrayList<Film> responseEntity = new ArrayList<>(jdbcFilmRepository.getPopular(10L));
        assertNotNull(responseEntity);
        assertEquals(3, responseEntity.size());
        assertEquals(film2Id, responseEntity.get(0).getId());
        assertEquals(film3Id, responseEntity.get(1).getId());
        assertEquals(film1Id, responseEntity.get(2).getId());
    }

    @Test
    void getPopularCount1() {
        Film film1 = getTestFilm(1);
        long film1Id = jdbcFilmRepository.create(film1).getId();
        Film film2 = getTestFilm(2);
        long film2Id = jdbcFilmRepository.create(film2).getId();

        User user1 = getTestUser(1);
        long user1Id = jdbcUserRepository.create(user1).getId();
        User user2 = getTestUser(2);
        long user2Id = jdbcUserRepository.create(user2).getId();
        User user3 = getTestUser(3);
        long user3Id = jdbcUserRepository.create(user3).getId();

        jdbcFilmRepository.addLike(film1Id, user1Id);
        jdbcFilmRepository.addLike(film1Id, user2Id);
        jdbcFilmRepository.addLike(film1Id, user3Id);

        jdbcFilmRepository.addLike(film2Id, user1Id);
        jdbcFilmRepository.addLike(film2Id, user2Id);

        ArrayList<Film> responseEntity = new ArrayList<>(jdbcFilmRepository.getPopular(1L));
        assertNotNull(responseEntity);
        assertEquals(1, responseEntity.size());
        assertEquals(film1Id, responseEntity.getFirst().getId());
    }
}
