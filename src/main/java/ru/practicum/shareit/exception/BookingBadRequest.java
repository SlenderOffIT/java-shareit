package ru.practicum.shareit.exception;

public class BookingBadRequest extends RuntimeException {
    public BookingBadRequest(String message) {
        super(message);
    }
}
