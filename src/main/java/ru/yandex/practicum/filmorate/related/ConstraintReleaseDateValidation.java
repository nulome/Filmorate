package ru.yandex.practicum.filmorate.related;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class ConstraintReleaseDateValidation implements ConstraintValidator<ConstraintReleaseDate, LocalDate> {
    private static final LocalDate FILM_BIRTHDAY = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        return localDate.isAfter(FILM_BIRTHDAY);
    }
}
