package ru.practicum.shareit.util;

public enum Constant {

    LOG_LIST_STATUS("Выводим список бронирований со статусом бронирования {}."),
    NOT_FOUND_USER("Пользователя с таким id {} не существует."),
    NOT_FOUND_ITEM("Предмета с id {} не существует"),
    NOT_FOUND_BOOKING("Бронирования с id {} не существует.");

    private final String value;

    Constant(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
