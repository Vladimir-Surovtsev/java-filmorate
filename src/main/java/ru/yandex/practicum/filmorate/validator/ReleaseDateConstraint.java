package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ReleaseDateValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReleaseDateConstraint {
    String message() default "Film realise must be after 1895";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
