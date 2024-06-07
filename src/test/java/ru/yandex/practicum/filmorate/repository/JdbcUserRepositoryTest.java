package ru.yandex.practicum.filmorate.repository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
class JdbcUserRepositoryTest {
    @Autowired
    private final JdbcUserRepository jdbcUserRepository;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @AllArgsConstructor
    static class ExpectedViolation {
        String propertyPath;
        String message;
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
                    .birthday(LocalDate.of(2000, 2, 20))
                    .build();
            case 3 -> User.builder()
                    .name("User 3")
                    .email("user3@ya.ru")
                    .login("userLogin3")
                    .birthday(LocalDate.of(2000, 2, 20))
                    .build();
            default -> null;
        };
    }

    @Test
    void findAll() {
        User user = getTestUser(1);
        jdbcUserRepository.create(user);
        User user2 = getTestUser(2);
        jdbcUserRepository.create(user2);

        List<User> responseEntity = jdbcUserRepository.findAll();
        assertNotNull(responseEntity);
        assertEquals(2, responseEntity.size());
    }

    @Test
    void create() {
        User user = getTestUser(1);
        jdbcUserRepository.create(user);
        List<User> responseEntity = jdbcUserRepository.findAll();
        assertNotNull(responseEntity);
        assertEquals(1, responseEntity.size());
        assertEquals(user.getLogin(), responseEntity.iterator().next().getLogin());
        assertEquals(user.getEmail(), responseEntity.iterator().next().getEmail());
        assertEquals(user.getName(), responseEntity.iterator().next().getName());
        assertEquals(user.getBirthday(), responseEntity.iterator().next().getBirthday());
    }

    @Test
    void update() {
        User user = getTestUser(1);
        long userId = jdbcUserRepository.create(user).getId();
        List<User> responseEntity;
        User newUser = getTestUser(2);
        newUser.setId(userId);
        jdbcUserRepository.update(newUser);
        responseEntity = jdbcUserRepository.findAll();
        assertNotNull(responseEntity);
        assertEquals(1, responseEntity.size());
        assertEquals(newUser.getId(), responseEntity.iterator().next().getId());
        assertEquals(newUser.getLogin(), responseEntity.iterator().next().getLogin());
        assertEquals(newUser.getEmail(), responseEntity.iterator().next().getEmail());
        assertEquals(newUser.getName(), responseEntity.iterator().next().getName());
        assertEquals(newUser.getBirthday(), responseEntity.iterator().next().getBirthday());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "mail",
            "@ya.ru",
            "ya.ru",
            "m   ail@ya.ru",
            ".mail@ya.ru",
            "mail@ya.ru.",
            "m@il@ya.ru"
    })
    void createUserWithNotCorrectEMail(String email) {
        User user = getTestUser(1);
        user.setEmail(email);

        List<ConstraintViolation<User>> violations = new ArrayList<>(validator.validate(user));

        ExpectedViolation expectedViolation = new ExpectedViolation(
                "email", "must be a well-formed email address");
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
    void createUserWithNullEMail() {
        User user = getTestUser(1);
        user.setEmail(null);

        List<ConstraintViolation<User>> violations = new ArrayList<>(validator.validate(user));

        ExpectedViolation expectedViolation = new ExpectedViolation(
                "email", "must not be blank");
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
    void createUserWithBlankEMail() {
        User user = getTestUser(1);
        user.setEmail("");

        List<ConstraintViolation<User>> violations = new ArrayList<>(validator.validate(user));

        ExpectedViolation expectedViolation = new ExpectedViolation(
                "email", "must not be blank");
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
    void createUserWithNullLogin() {
        User user = getTestUser(1);
        user.setLogin(null);

        List<ConstraintViolation<User>> violations = new ArrayList<>(validator.validate(user));

        ExpectedViolation expectedViolation = new ExpectedViolation(
                "login", "must not be blank");
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
    void createUserWithBlankLogin() {
        User user = getTestUser(1);
        user.setLogin("");

        List<ConstraintViolation<User>> violations = new ArrayList<>(validator.validate(user));

        ExpectedViolation expectedViolation = new ExpectedViolation(
                "login", "must not be blank");
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
    void createUserWithBirthdayAfterNow() {
        User user = getTestUser(1);
        user.setBirthday(LocalDate.now().plusDays(1));

        List<ConstraintViolation<User>> violations = new ArrayList<>(validator.validate(user));
        System.out.println(violations);
        ExpectedViolation expectedViolation = new ExpectedViolation(
                "birthday", "must be a date in the past or in the present");
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
    void createUserWithBirthdayNow() {
        User user = getTestUser(1);
        user.setBirthday(LocalDate.now());

        jdbcUserRepository.create(user);
        List<User> responseEntity = jdbcUserRepository.findAll();
        assertNotNull(responseEntity);
        assertEquals(1, responseEntity.size());
        assertEquals(user.getLogin(), responseEntity.iterator().next().getLogin());
        assertEquals(user.getEmail(), responseEntity.iterator().next().getEmail());
        assertEquals(user.getName(), responseEntity.iterator().next().getName());
        assertEquals(user.getBirthday(), responseEntity.iterator().next().getBirthday());
    }

    @Test
    void addFriend() {
        User user = getTestUser(1);
        long user1Id = jdbcUserRepository.create(user).getId();
        User user2 = getTestUser(2);
        long user2Id = jdbcUserRepository.create(user2).getId();
        jdbcUserRepository.addToFriends(user1Id, user2Id);
        List<User> responseEntity = new ArrayList<>(jdbcUserRepository.findAll());
        assertNotNull(responseEntity);
        assertEquals(2, responseEntity.size());
        assertTrue(responseEntity.getFirst().getFriends().contains(new Friend(user2Id, 2)));
    }

    @Test
    void deleteFriend() {
        User user = getTestUser(1);
        long user1Id = jdbcUserRepository.create(user).getId();
        User user2 = getTestUser(2);
        long user2Id = jdbcUserRepository.create(user2).getId();
        jdbcUserRepository.addToFriends(user1Id, user2Id);
        jdbcUserRepository.deleteFromFriends(user1Id, user2Id);
        List<User> responseEntity = new ArrayList<>(jdbcUserRepository.findAll());
        assertNotNull(responseEntity);
        assertEquals(2, responseEntity.size());
        assertFalse(responseEntity.getFirst().getFriends().contains(new Friend(user2Id, 2)));
    }

    @Test
    void findAllFriends() {
        User user = getTestUser(1);
        long user1Id = jdbcUserRepository.create(user).getId();
        User user2 = getTestUser(2);
        long user2Id = jdbcUserRepository.create(user2).getId();
        User user3 = getTestUser(3);
        long user3Id = jdbcUserRepository.create(user3).getId();
        jdbcUserRepository.addToFriends(user1Id, user2Id);
        jdbcUserRepository.addToFriends(user1Id, user3Id);
        List<User> responseEntity = new ArrayList<>(jdbcUserRepository.findAllFriends(user1Id));
        assertNotNull(responseEntity);
        assertEquals(2, responseEntity.size());
        assertEquals(responseEntity.get(0).getId(), user2Id);
        assertEquals(responseEntity.get(1).getId(), user3Id);
    }

    @Test
    void findCommonFriends() {
        User user = getTestUser(1);
        long user1Id = jdbcUserRepository.create(user).getId();
        User user2 = getTestUser(2);
        long user2Id = jdbcUserRepository.create(user2).getId();
        User user3 = getTestUser(3);
        long user3Id = jdbcUserRepository.create(user3).getId();
        jdbcUserRepository.addToFriends(user1Id, user2Id);
        jdbcUserRepository.addToFriends(user1Id, user3Id);
        List<User> responseEntity = new ArrayList<>(jdbcUserRepository.findCommonFriends(user2Id, user3Id));
        assertNotNull(responseEntity);
        assertEquals(1, responseEntity.size());
        assertEquals(responseEntity.getFirst().getId(), user1Id);
    }
}
