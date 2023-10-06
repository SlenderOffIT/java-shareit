package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailConflictException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.MapperUserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.dto.MapperUserDto.toUser;
import static ru.practicum.shareit.user.dto.MapperUserDto.toUserDto;
import static ru.practicum.shareit.util.Constant.NOT_FOUND_USER;
import static ru.practicum.shareit.util.Validation.validate;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        log.debug("Обрабатываем запрос на просмотр списка всех пользователей.");

        return userRepository.findAll().stream()
                .map(MapperUserDto::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(int id) {
        log.debug("Обрабатываем запрос на просмотр пользователя с id {}.", id);

        if (!userRepository.existsById(id)) {
            log.warn(NOT_FOUND_USER, id);
            throw new UserNotFoundException(String.format("Пользователя с таким id %d не существует.", id));
        }
        return toUserDto(userRepository.getReferenceById(id));
    }

    @Override
    public UserDto postUser(UserDto userDto) {
        log.debug("Обрабатываем запрос на создание пользователя с email {}.", userDto.getEmail());
        validate(userDto);
        User user;
        try {
            user = userRepository.save(toUser(userDto));
        } catch (Throwable e) {
            log.warn("Пользователь с таким email {} уже существует", userDto.getEmail());
            throw new DataIntegrityViolationException(String.format("Пользователь с таким email %s уже существует", userDto.getEmail()));
        }
        return toUserDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto, int id) {
        log.debug("Обрабатываем запрос на изменение пользователя с email {}.", userDto.getEmail());

        if (userDto.getEmail() != null) {
            validate(userDto);
        }

        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User userUpdate = user.get();
            userUpdate.setId(id);
            if (userDto.getName() != null) {
                userUpdate.setName(userDto.getName());
            }
            if (userDto.getEmail() != null && !userUpdate.getEmail().equals(userDto.getEmail())) {
                if (userRepository.emailIsExist(userDto.getEmail())) {
                    throw new EmailConflictException(String.format("Пользователь с таким email %s уже существует.", userUpdate.getEmail()));
                }
                userUpdate.setEmail(userDto.getEmail());
            }
            return toUserDto(userRepository.save(userUpdate));
        } else {
            log.warn(NOT_FOUND_USER, id);
            throw new UserNotFoundException(String.format("С таким id %d пользователя не существует.", id));
        }
    }

    @Override
    public void deleteUser(int id) {
        log.debug("Обрабатываем запрос на удаление пользователя с id {}.", id);
        userRepository.deleteById(id);
    }
}
