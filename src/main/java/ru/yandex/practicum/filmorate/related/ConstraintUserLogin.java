package ru.yandex.practicum.filmorate.related;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(
        validatedBy = {ConstraintUserLoginValidation.class}
)
public @interface ConstraintUserLogin {
    String message() default "Логин не может быть пустым и содержать пробел";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
