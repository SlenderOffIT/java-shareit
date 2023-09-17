package ru.practicum.shareit.booking;

public enum BookingStatusEnum {

    WAITING("WAITING"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED"),
    CANCELED("CANCELED");

    private String value;

    BookingStatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
