package ru.practicum.shareit.user.repository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmailConflictException;
import ru.practicum.shareit.user.dto.MapperUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
@RequiredArgsConstructor
public class InMemoryUserRepositoryImpl implements UserRepository {

    @Getter
    private final Map<Integer, User> storageUsers = new HashMap<>();
    @Getter
    private final Set<String> storageEmail = new HashSet<>();
    private int id = 1;

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> users = new ArrayList<>();
        for (User user: storageUsers.values()) {
            UserDto userDto = MapperUserDto.toUserDto(user);
            users.add(userDto);
        }
        return users;
    }

    @Override
    public UserDto getUserById(int id) {
        return MapperUserDto.toUserDto(storageUsers.get(id));
    }

    @Override
    public UserDto save(UserDto userDto) {
        User user = MapperUserDto.toUser(userDto);
        user.setId(id++);
        storageUsers.put(user.getId(), user);
        storageEmail.add(userDto.getEmail());
        return MapperUserDto.toUserDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto, int id) {
        User user = storageUsers.get(id);
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !user.getEmail().equals(userDto.getEmail())) {
            if (emailIsExist(userDto.getEmail())) {
                throw new EmailConflictException(String.format("Пользователь с таким email %s уже существует.", user.getEmail()));
            }
            storageEmail.remove(user.getEmail());
            storageEmail.add(userDto.getEmail());
            user.setEmail(userDto.getEmail());
        }
        storageUsers.put(user.getId(), user);
        return MapperUserDto.toUserDto(user);
    }

    @Override
    public void deleteUser(int id) {
        UserDto user = getUserById(id);
        storageEmail.remove(user.getEmail());
        storageUsers.remove(id);
    }

    @Override
    public boolean emailIsExist(String email) {
        return storageEmail.contains(email);
    }

    @Override
    public boolean userIsExist(int id) {
        return storageUsers.containsKey(id);
    }
}
