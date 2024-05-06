package ru.yandex.practicum.filmorate.related;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ConstraintUserLoginValidation implements ConstraintValidator<ConstraintUserLogin, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return !s.contains(" ") && !s.isEmpty();
    }
}
