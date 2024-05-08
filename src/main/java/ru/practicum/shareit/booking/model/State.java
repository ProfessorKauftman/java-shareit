package ru.practicum.shareit.booking.model;

import java.util.Arrays;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static State from(String bookingState) {
        return Arrays.stream(State.values())
                .filter(value -> value.name().equals(bookingState))
                .findFirst()
                .orElse(null);
    }
}
