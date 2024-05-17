package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class FilmControllerTest {

    UserStorage userStorage = new InMemoryUserStorage();
    FilmStorage storage = new InMemoryFilmStorage(userStorage);
    FilmService service = new FilmService(storage);
    FilmController filmController = new FilmController(service);

    final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @AllArgsConstructor
    static class ExpectedViolation {
        String propertyPath;
        String message;
    }

    @Test
    void validateFilm() {
        final Film film = Film.builder()
                .name("Test film")
                .description("Film description")
                .releaseDate(LocalDate.of(2008, 5, 31))
                .duration(155)
                .build();
        filmController.create(film);
        validator.validate(filmController.findAll().getFirst());
    }

    @Test
    void validateFilmEmptyNameFail() {
        final Film film = Film.builder()
                .name("")
                .description("Film description")
                .releaseDate(LocalDate.of(2008, 5, 31))
                .duration(155)
                .build();

        List<ConstraintViolation<Film>> violations = new ArrayList<>(validator.validate(film));
        ExpectedViolation expectedViolation = new ExpectedViolation("name", "must not be blank");
        assertEquals(expectedViolation.propertyPath, violations.getFirst().getPropertyPath().toString());
        assertEquals(expectedViolation.message, violations.getFirst().getMessage());
        assertEquals(1, violations.size());
    }

    @Test
    void validateFilmTooLongDescriptionFail() {
        final Film film = Film.builder()
                .name("Test film")
                .description("Film description".repeat(22))
                .releaseDate(LocalDate.of(2008, 5, 31))
                .duration(155)
                .build();

        List<ConstraintViolation<Film>> violations = new ArrayList<>(validator.validate(film));
        ExpectedViolation expectedViolation = new ExpectedViolation("description",
                "size must be between 0 and 200");
        assertEquals(expectedViolation.propertyPath, violations.getFirst().getPropertyPath().toString());
        assertEquals(expectedViolation.message, violations.getFirst().getMessage());
        assertEquals(1, violations.size());
    }

    @Test
    void validateFilmReleaseDateFail() {
        final Film film = Film.builder()
                .name("Test film")
                .description("Film description")
                .releaseDate(LocalDate.of(1894, 5, 31))
                .duration(155)
                .build();

        List<ConstraintViolation<Film>> violations = new ArrayList<>(validator.validate(film));
        ExpectedViolation expectedViolation = new ExpectedViolation("releaseDate",
                "Film realise must be after 1895");
        assertEquals(expectedViolation.propertyPath, violations.getFirst().getPropertyPath().toString());
        assertEquals(expectedViolation.message, violations.getFirst().getMessage());
        assertEquals(1, violations.size());
    }

    @Test
    void validateFilmDurationFail() {
        final Film film = Film.builder()
                .name("Test film")
                .description("Film description")
                .releaseDate(LocalDate.of(2008, 5, 31))
                .duration(0)
                .build();

        List<ConstraintViolation<Film>> violations = new ArrayList<>(validator.validate(film));
        ExpectedViolation expectedViolation = new ExpectedViolation("duration",
                "must be greater than 0");
        assertEquals(expectedViolation.propertyPath, violations.getFirst().getPropertyPath().toString());
        assertEquals(expectedViolation.message, violations.getFirst().getMessage());
        assertEquals(1, violations.size());
    }

    @Test
    void createFilmTest() {
        final Film film1 = Film.builder()
                .name("Test film1")
                .description("Film description")
                .releaseDate(LocalDate.of(2008, 5, 31))
                .duration(155)
                .build();
        final Film film2 = Film.builder()
                .name("Test film2")
                .description("Film description")
                .releaseDate(LocalDate.of(2008, 5, 31))
                .duration(155)
                .build();

        filmController.create(film1);
        filmController.create(film2);

        final Film savedFilm1 = filmController.findAll().get(Math.toIntExact(film1.getId()) - 1);
        final Film savedFilm2 = filmController.findAll().get(Math.toIntExact(film2.getId()) - 1);
        final List<Film> films = filmController.findAll();

        assertNotNull(films, "Информация о фильмах не возвращается");
        assertEquals(film1, savedFilm1, "Информация о фильме не соответствует");
        assertEquals(film2, savedFilm2, "Информация о фильме не соответствует");
    }

    @Test
    void updateFilmTest() {
        final Film film1 = Film.builder()
                .name("Test film1")
                .description("Film description")
                .releaseDate(LocalDate.of(2008, 5, 31))
                .duration(155)
                .build();
        final Film film2 = Film.builder()
                .name("Test film2")
                .description("Film description")
                .releaseDate(LocalDate.of(2011, 5, 31))
                .duration(122)
                .build();

        filmController.create(film1);
        film2.setId(film1.getId());
        filmController.update(film2);

        final Film updatedFilm = filmController.findAll().get(Math.toIntExact(film1.getId()) - 1);

        assertEquals(film2, updatedFilm, "Информация о фильме не соответствует");
    }

    @Test
    void validateIdNotSetForUpdateFail() {
        final Film film1 = Film.builder()
                .name("Test film1")
                .description("Film description")
                .releaseDate(LocalDate.of(2008, 5, 31))
                .duration(155)
                .build();
        final Film film2 = Film.builder()
                .name("Test film2")
                .description("Film description")
                .releaseDate(LocalDate.of(2011, 5, 31))
                .duration(122)
                .build();

        filmController.create(film1);

        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class,
                () -> filmController.update(film2), "Ожидалось получение исключения");
        assertEquals("Фильм с id = 0 не найден", thrown.getMessage());
    }

}
