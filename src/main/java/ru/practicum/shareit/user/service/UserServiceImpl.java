package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailConflictException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static ru.practicum.shareit.util.Validation.validate;

@Slf4j
@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        log.debug("Обрабатываем запрос на просмотр списка всех пользователей.");
        return userRepository.getAllUsers();
    }

    @Override
    public UserDto getUserById(int id) {
        log.debug("Обрабатываем запрос на просмотр пользователя с id {}.", id);
        return userRepository.getUserById(id);
    }

    @Override
    public UserDto postUser(UserDto user) {
        log.debug("Обрабатываем запрос на создание пользователя с email {}.", user.getEmail());
        if (userRepository.emailIsExist(user.getEmail())) {
            log.debug("Попытка создать пользователя c уже существующим email {}.", user.getEmail());
            throw new EmailConflictException(String.format("Пользователь с таким email %s уже существует.", user.getEmail()));
        }
        validate(user);
        return userRepository.save(user);
    }

    @Override
    public UserDto updateUser(UserDto user, int id) {
        log.debug("Обрабатываем запрос на изменение пользователя с email {}.", user.getEmail());
        if (user.getEmail() != null) {
            validate(user);
        }
        return userRepository.updateUser(user, id);
    }

    @Override
    public void deleteUser(int id) {
        log.debug("Обрабатываем запрос на удаление пользователя с id {}.", id);
        userRepository.deleteUser(id);
    }
}
