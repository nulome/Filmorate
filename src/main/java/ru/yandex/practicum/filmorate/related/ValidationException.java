package ru.yandex.practicum.filmorate.related;


import java.io.IOException;

public class ValidationException extends RuntimeException {
    public ValidationException() {
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(final String message, final IOException e) {
        super(message, e);
    }
}
