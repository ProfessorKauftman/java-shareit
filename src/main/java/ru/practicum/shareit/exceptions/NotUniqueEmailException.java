package ru.practicum.shareit.exceptions;

public class NotUniqueEmailException extends RuntimeException {
    public NotUniqueEmailException(String msg) {
        super(msg);
    }
}
