package ru.practicum.shareit.exception;

public class RequestItemBadRequest extends RuntimeException {
    public RequestItemBadRequest(String message) {
        super(message);
    }
}

