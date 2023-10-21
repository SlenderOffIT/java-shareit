package ru.practicum.shareit.booking;

public enum BookingStatusEnum {

    WAITING("WAITING"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED"),
    CANCELED("CANCELED"),
    CURRENT("CURRENT"),
    PAST("PAST"),
    ALL("ALL"),
    FUTURE("FUTURE"),
    UNSUPPORTED_STATUS("UNSUPPORTED_STATUS");

    private String value;

    BookingStatusEnum(String value) {
        this.value = value;
    }
}
