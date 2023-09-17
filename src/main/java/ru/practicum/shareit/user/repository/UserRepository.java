package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserRepository {
    List<UserDto> getAllUsers();
    UserDto getUserById(int id);
    UserDto save(UserDto user);
    UserDto updateUser(UserDto user, int id);
    void deleteUser(int id);
    Map<Integer, User> getStorageUsers();
    public boolean emailIsExist(String email);
    public boolean userIsExist(int id);
}
