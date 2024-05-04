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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FilmControllerTest {
    static FilmController filmController = new FilmController();

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @AllArgsConstructor
    static class ExpectedViolation {
        String propertyPath;
        String message;
    }

    @Test
    void validateFilm() {
        generateDefaultFilm();
        validator.validate(filmController.getAll().getFirst());
    }

    @Test
    void validateFilmEmptyNameFail() {
        final Film film = generateCustomFilm("", "Film description", LocalDate.of(2010, 4,
                30), 400);

        List<ConstraintViolation<Film>> violations = new ArrayList<>(validator.validate(film));
        ExpectedViolation expectedViolation = new ExpectedViolation("name", "must not be blank");
        assertEquals(expectedViolation.propertyPath, violations.getFirst().getPropertyPath().toString());
        assertEquals(expectedViolation.message, violations.getFirst().getMessage());
        assertEquals(1, violations.size());
    }

    @Test
    void validateFilmTooLongDescriptionFail() {
        final Film film = generateCustomFilm("Test film", "Film description".repeat(20),
                LocalDate.of(2010, 5, 31), 155);

        List<ConstraintViolation<Film>> violations = new ArrayList<>(validator.validate(film));
        ExpectedViolation expectedViolation = new ExpectedViolation("description",
                "size must be between 0 and 200");
        assertEquals(expectedViolation.propertyPath, violations.getFirst().getPropertyPath().toString());
        assertEquals(expectedViolation.message, violations.getFirst().getMessage());
        assertEquals(1, violations.size());
    }

    @Test
    void validateFilmReleaseDateFail() {
        final Film film = generateCustomFilm("Test film", "Film description",
                LocalDate.of(1894, 1, 1), 150);

        List<ConstraintViolation<Film>> violations = new ArrayList<>(validator.validate(film));
        ExpectedViolation expectedViolation = new ExpectedViolation("releaseDate",
                "Film realise must be after 1895");
        assertEquals(expectedViolation.propertyPath, violations.getFirst().getPropertyPath().toString());
        assertEquals(expectedViolation.message, violations.getFirst().getMessage());
        assertEquals(1, violations.size());
    }

    @Test
    void validateFilmDurationFail() {
        final Film film = generateCustomFilm("Test film", "Film description",
                LocalDate.of(2008, 5, 31), -155);

        List<ConstraintViolation<Film>> violations = new ArrayList<>(validator.validate(film));
        ExpectedViolation expectedViolation = new ExpectedViolation("duration",
                "must be greater than 0");
        assertEquals(expectedViolation.propertyPath, violations.getFirst().getPropertyPath().toString());
        assertEquals(expectedViolation.message, violations.getFirst().getMessage());
        assertEquals(1, violations.size());
    }

    @Test
    void createFilmTest() {
        final Film film1 = generateCustomFilm("Test film 1", "Film description",
                LocalDate.of(2008, 5, 31), 155);
        final Film film2 = generateCustomFilm("Test film 2", "Film description",
                LocalDate.of(2008, 5, 31), 155);

        filmController.create(film1);
        filmController.create(film2);

        final Film savedFilm1 = filmController.getAll().get(Math.toIntExact(film1.getId()) - 1);
        final Film savedFilm2 = filmController.getAll().get(Math.toIntExact(film2.getId()) - 1);
        final List<Film> films = filmController.getAll();

        assertNotNull(films, "Информация о фильмах не возвращается");
        assertEquals(film1, savedFilm1, "Информация о фильме не соответствует");
        assertEquals(film2, savedFilm2, "Информация о фильме не соответствует");
    }

    @Test
    void updateFilmTest() {
        final Film film1 = generateCustomFilm("Test film 1", "Film description",
                LocalDate.of(2008, 5, 31), 135);
        final Film film2 = generateCustomFilm("Test film 2", "Film description",
                LocalDate.of(2000, 5, 1), 155);

        filmController.create(film1);
        film2.setId(film1.getId());
        filmController.update(film2);

        final Film updatedFilm = filmController.getAll().get(Math.toIntExact(film1.getId()) - 1);

        assertEquals(film2, updatedFilm, "Информация о фильме не соответствует");
    }

    @Test
    void validateIdNotSetForUpdateFail() {
        final Film film1 = generateCustomFilm("Test film 1", "Film description",
                LocalDate.of(2008, 5, 31), 135);
        final Film film2 = generateCustomFilm("Test film 2", "Film description",
                LocalDate.of(2000, 5, 1), 155);

        filmController.create(film1);

        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class,
                () -> filmController.update(film2), "Ожидалось получение исключения");
        assertEquals("Фильм с id = 0 не найден", thrown.getMessage());
    }

    private void generateDefaultFilm() {
        Stream.generate(() -> Film.builder()
                        .id(0)
                        .name("Test film")
                        .description("Film description")
                        .releaseDate(LocalDate.of(2008, 5, 31))
                        .duration(155)
                        .build())
                .limit(1)
                .forEach(filmController::create);
    }

    private Film generateCustomFilm(
            String name,
            String description,
            LocalDate release,
            int duration) {
        return Film.builder()
                .name(name)
                .description(description)
                .releaseDate(release)
                .duration(duration)
                .build();
    }
}
