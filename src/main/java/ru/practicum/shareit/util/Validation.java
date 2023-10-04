package ru.practicum.shareit.util;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookingDtoJson;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class Validation {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                    "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

    public static void validate(final UserDto user) {
        if (user.getEmail() == null) {
            log.debug("Пользователь не ввел email.");
            throw new ValidationException("Некорректный email адрес.");
        }
        Matcher matcher = EMAIL_PATTERN.matcher(user.getEmail());
        if (!matcher.matches()) {
            log.debug("Пользователь ввел не правильный email " + user.getEmail());
            throw new ValidationException("Некорректный email адрес.");
        }
    }

    public static void validate(final ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isEmpty()) {
            log.debug("Поступил предмет на создание без названия.");
            throw new ValidationException("У предмета должно быть название.");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isEmpty()) {
            log.debug("Поступил предмет на создание без описания.");
            throw new ValidationException("У предмета должно быть описание.");
        }
        if (itemDto.getAvailable() == null) {
            log.debug("Поступил предмет на создание без описания наличия.");
            throw new ValidationException("У предмета должно быть указано наличие.");
        }
    }

    public static void validate(final BookingDtoJson bookingDto) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        if (bookingDto.getStart() == null) {
            log.debug("Время старта бронирования не должно быть null.");
            throw new ValidationException("Время старта бронирования не должно быть null.");
        }
        if (bookingDto.getEnd() == null) {
            log.debug("Время окончания бронирования не должно быть null.");
            throw new ValidationException("Время окончания бронирования не должно быть null.");
        }
        if (bookingDto.getStart().isBefore(currentDateTime)) {
            log.debug("Время старта бронирования не должно быть в прошлом");
            throw new ValidationException("Время старта бронирования не должно быть в прошлом");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            log.debug("Время окончания бронирования не должно быть раньше старта.");
            throw new ValidationException("Время окончания бронирования не должно быть раньше старта.");
        }
        if (bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            log.debug("Время старта бронирования не должно быть раньше окончания.");
            throw new ValidationException("Время старта бронирования не должно быть раньше окончания.");
        }
        if (bookingDto.getEnd().isBefore(currentDateTime)) {
            log.debug("Время окончания бронирования не должно быть в прошлом.");
            throw new ValidationException("Время окончания бронирования не должно быть в прошлом.");
        }
    }
}
