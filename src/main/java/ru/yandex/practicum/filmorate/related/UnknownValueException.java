package ru.yandex.practicum.filmorate.related;


public class UnknownValueException extends RuntimeException {
    public UnknownValueException(String message) {
        super(message);
    }

    public UnknownValueException(final String message, final Throwable e) {
        super(message, e);
    }
}
