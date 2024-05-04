package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    static UserController userController = new UserController();
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @AllArgsConstructor
    static class ExpectedViolation {
        String propertyPath;
        String message;
    }

    @Test
    void validateUser() {
        generateDefaultUser();
        validator.validate(userController.getAll().getFirst());
    }

    @Test
    void validateUserEmptyEmailFail() {
        final User user = generateCustomUser("", "user", "User Name", LocalDate.of(1999, 5,
                24));

        List<ConstraintViolation<User>> violations = new ArrayList<>(validator.validate(user));
        ExpectedViolation expectedViolation = new ExpectedViolation("email", "must not be blank");
        assertEquals(expectedViolation.propertyPath, violations.getFirst().getPropertyPath().toString());
        assertEquals(expectedViolation.message, violations.getFirst().getMessage());
        assertEquals(1, violations.size());
    }

    @Test
    void validateUserEmailWithoutAtFail() {
        final User user = generateCustomUser("user.yandex.ru", "user", "User Name", LocalDate.of(1999,
                5, 24));

        List<ConstraintViolation<User>> violations = new ArrayList<>(validator.validate(user));
        ExpectedViolation expectedViolation = new ExpectedViolation("email",
                "must be a well-formed email address");
        assertEquals(expectedViolation.propertyPath, violations.getFirst().getPropertyPath().toString());
        assertEquals(expectedViolation.message, violations.getFirst().getMessage());
        assertEquals(1, violations.size());
    }

    @Test
    void validateUserLoginFail() {
        final User user = generateCustomUser("user@yandex.ru", "", "User Name", LocalDate.of(1999,
                5, 24));

        List<ConstraintViolation<User>> violations = new ArrayList<>(validator.validate(user));
        ExpectedViolation expectedViolation = new ExpectedViolation("login", "must not be blank");
        assertEquals(expectedViolation.propertyPath, violations.getFirst().getPropertyPath().toString());
        assertEquals(expectedViolation.message, violations.getFirst().getMessage());
        assertEquals(1, violations.size());
    }

    @Test
    void validateUserNameAsLoginTest() {
        final User user = generateCustomUser("user@yandex.ru", "user", null, LocalDate.of(1999,
                5, 24));
        userController.create(user);

        validator.validate(user);

        assertEquals(user.getLogin(), user.getName());
    }

    @Test
    void validateUserBirthdayFail() {
        final User user = generateCustomUser("user@yandex.ru", "user", "User Name",
                LocalDate.of(3000, 5, 24));

        List<ConstraintViolation<User>> violations = new ArrayList<>(validator.validate(user));
        ExpectedViolation expectedViolation = new ExpectedViolation("birthday",
                "must be a date in the past or in the present");
        assertEquals(expectedViolation.propertyPath, violations.getFirst().getPropertyPath().toString());
        assertEquals(expectedViolation.message, violations.getFirst().getMessage());
        assertEquals(1, violations.size());
    }

    @Test
    void createUserTest() {
        final User user1 = generateCustomUser("user@yandex.ru", "user 1", "User Name",
                LocalDate.of(1999, 5, 24));
        final User user2 = generateCustomUser("user@yandex.ru", "user 2", "User Name",
                LocalDate.of(1999, 5, 24));

        userController.create(user1);
        userController.create(user2);

        final User savedUser1 = userController.getAll().get(Math.toIntExact(user1.getId()) - 1);
        final User savedUser2 = userController.getAll().get(Math.toIntExact(user2.getId()) - 1);
        final List<User> users = userController.getAll();

        assertNotNull(users, "Информация о пользователях не возвращается");
        assertEquals(user1, savedUser1, "Информация о пользователе не соответствует");
        assertEquals(user2, savedUser2, "Информация о пользователе не соответствует");
    }

    @Test
    void updateUserTest() {
        final User user1 = generateCustomUser("user@yandex.ru", "user 1", "User Name",
                LocalDate.of(1999, 5, 24));
        final User user2 = generateCustomUser("user@yandex.ru", "user 2", "User Name",
                LocalDate.of(1999, 5, 24));

        userController.create(user1);
        user2.setId(user1.getId());
        userController.update(user2);

        final User updatedUser = userController.getAll().get(Math.toIntExact(user1.getId()) - 1);

        assertEquals(user2, updatedUser, "Информация о пользователе не соответствует");
    }

    @Test
    void validateIdNotSetForUpdateFail() {
        final User user1 = generateCustomUser("user@yandex.ru", "user 1", "User Name",
                LocalDate.of(1999, 5, 24));
        final User user2 = generateCustomUser("user@yandex.ru", "user 2", "User Name",
                LocalDate.of(1999, 5, 24));

        userController.create(user1);

        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class,
                () -> userController.update(user2), "Ожидалось получение исключения");
        assertEquals("Пользователь с id = 0 не найден", thrown.getMessage());
    }

    private void generateDefaultUser() {
        Stream.generate(() -> User.builder()
                        .id(0)
                        .email("user@yandex.ru")
                        .login("user")
                        .name("User Name")
                        .birthday(LocalDate.of(1999, 5, 24))
                        .build())
                .limit(1)
                .forEach(userController::create);
    }

    private User generateCustomUser(
            String email,
            String login,
            String name,
            LocalDate birthday) {
        return User.builder()
                .email(email)
                .login(login)
                .name(name)
                .birthday(birthday)
                .build();
    }
}
