package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class ReleaseDateValidator implements ConstraintValidator<ReleaseDateConstraint, LocalDate> {
    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return value.isAfter(LocalDate.of(1895, 12, 28));
    }
}
