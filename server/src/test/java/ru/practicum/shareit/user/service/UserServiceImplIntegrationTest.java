package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    public void postUser() {
        UserDto user = new UserDto("Вася", "asdfgh@gmail.com");
        UserDto createUser = userService.postUser(user);
        UserDto user2 = new UserDto("Петя", "dfgh@gmail.com");
        UserDto createUser2 = userService.postUser(user2);

        assertEquals(1, createUser.getId());
        assertEquals(2, createUser2.getId());
    }

    @Test
    public void getAllUsers() {
        UserDto user = new UserDto("Вася", "asdfgh@gmail.com");
        UserDto createUser = userService.postUser(user);
        UserDto user2 = new UserDto("Петя", "dfgh@gmail.com");
        UserDto createUser2 = userService.postUser(user2);
        UserDto user3 = new UserDto("Ваня", "gggeeef@mail.ru");
        UserDto createUser3 = userService.postUser(user3);

        List<UserDto> userDtoList = userService.getAllUsers();

        assertEquals(3, userDtoList.size());
        assertEquals(createUser, userDtoList.get(0));
        assertEquals(createUser2, userDtoList.get(1));
        assertEquals(createUser3, userDtoList.get(2));
    }

    @Test
    public void getUserById() {
        UserDto user = new UserDto("Вася", "asdfgh@gmail.com");
        UserDto createUser = userService.postUser(user);
        UserDto user2 = new UserDto("Петя", "dfgh@gmail.com");
        UserDto createUser2 = userService.postUser(user2);

        UserDto userById1 = userService.getUserById(1);

        assertNotNull(userById1);
        assertEquals(userById1, createUser);

        UserDto userById2 = userService.getUserById(2);

        assertNotNull(userById2);
        assertEquals(userById2, createUser2);
    }

    @Test
    public void updateUser() {
        UserDto user = new UserDto("Вася", "asdfgh@gmail.com");
        userService.postUser(user);
        UserDto update = new UserDto("Петя", "dfgh@gmail.com");

        UserDto createUpdate = userService.updateUser(update, 1);

        assertEquals(update.getName(), createUpdate.getName());
        assertEquals(update.getEmail(), createUpdate.getEmail());
        assertEquals(1, createUpdate.getId());
    }

    @Test
    public void deleteUser() {
        UserDto user = new UserDto("Вася", "asdfgh@gmail.com");
        userService.postUser(user);
        userService.deleteUser(1);

        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserById(1);
        });
    }
}