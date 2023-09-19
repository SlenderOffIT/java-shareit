package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.debug("Поступил запрос на просмотр всех пользователей.");
        return userService.getAllUsers();
    }

    @GetMapping("{id}")
    public UserDto getUserById(@PathVariable int id) {
        log.debug("Поступил запрос на просмотр пользователя с id {}.", id);
        return userService.getUserById(id);
    }

    @PostMapping
    public UserDto postUser(@RequestBody UserDto user) {
        log.debug("Поступил запрос на создание пользователя с email {}.", user.getEmail());
        return userService.postUser(user);
    }

    @PatchMapping("{id}")
    public UserDto updateUser(@RequestBody UserDto user, @PathVariable int id) {
        log.debug("Поступил запрос на изменение пользователя с id {}.", id);
        return userService.updateUser(user, id);
    }

    @DeleteMapping("{id}")
    public void deleteUser(@PathVariable int id) {
        log.debug("Поступил запрос на удаление пользователя с id {}.", id);
        userService.deleteUser(id);
    }
}
