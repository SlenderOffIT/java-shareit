package ru.practicum.shareit.util;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

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

    public static void validate(final ItemDto itemDto, final int idUser, UserRepository userRepository) {
        if (!userRepository.userIsExist(idUser)) {
            log.debug("Такого пользователя с id {} не существует.", idUser);
            throw new ItemNotFoundException("Такого пользователя не существует.");
        }
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
}
