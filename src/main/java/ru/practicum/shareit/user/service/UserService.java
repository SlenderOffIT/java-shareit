package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
public interface UserService {

    List<UserDto> getAllUsers();

    UserDto getUserById(int id);

    UserDto postUser(UserDto user);

    UserDto updateUser(UserDto user, int id);

    void deleteUser(int id);
}
