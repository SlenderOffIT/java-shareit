package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.EmailConflictException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void before() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    public void getAllUsers() {
        User user = new User("Вася", "asdfgh@gmail.com");
        User user2 = new User("Петя", "dfgh@gmail.com");
        User user3 = new User("Ваня", "gggeeef@mail.ru");

        when(userRepository.findAll()).thenReturn(List.of(user, user2, user3));

        List<UserDto> userList = userService.getAllUsers();

        assertEquals(3, userList.size());
        assertEquals(user.getName(), userList.get(0).getName());
        assertEquals(user.getEmail(), userList.get(0).getEmail());
    }

    @Test
    public void getUserByIdWarn() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            UserDto userDto = userService.getUserById(3);
        });
    }

    @Test
    public void getUserByIdGood() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        UserDto userDto = userService.getUserById(1);

        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    public void createUserEmptyEmail() {
        UserDto user = new UserDto();
        user.setName("name");

        assertThrows(ValidationException.class, () -> {
            userService.postUser(user);
        });
    }

    @Test
    public void createUserFailEmail() {
        UserDto user = new UserDto();
        user.setName("name");
        user.setEmail("affsdf.re");

        assertThrows(ValidationException.class, () -> {
            userService.postUser(user);
        });
    }

    @Test
    public void createUserGood() {
        UserDto userDto = new UserDto(1, "Новый", "newuser@example.com");
        User user = new User(1, "Новый", "newuser@example.com");
        when(userRepository.save(any())).thenReturn(user);

        UserDto savedUserDto = userService.postUser(userDto);

        assertNotNull(savedUserDto);
        assertEquals(userDto.getId(), savedUserDto.getId());
        assertEquals(userDto.getName(), savedUserDto.getName());
        assertEquals(userDto.getEmail(), savedUserDto.getEmail());
    }

    @Test
    public void createUserDuplicateEmail() {
        UserDto userDto = new UserDto(1, "Новый", "newuser@example.com");

        when(userRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            userService.postUser(userDto);
        });
    }


    @Test
    public void updateUserFailEmail() {
        UserDto user = new UserDto();
        user.setId(1);
        user.setName("name");
        user.setEmail("affsdf.re");

        assertThrows(ValidationException.class, () -> {
            userService.updateUser(user, user.getId());
        });
    }

    @Test
    public void updateUserFailId() {
        UserDto user = new UserDto();
        user.setName("name");
        user.setEmail("asdff@maid.ru");

        assertThrows(UserNotFoundException.class, () -> {
            userService.updateUser(user, 2);
        });
    }

    @Test
    void updateUserGood() {
        User user = new User(1, "Вася", "vasya@example.com");
        UserDto userUpdateDto = new UserDto(1, "Новое имя", "newemail@example.com");
        UserDto expectedUserDto = new UserDto(1, "Новое имя", "newemail@example.com");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserDto resultUserDto = userService.updateUser(userUpdateDto, 1);

        assertEquals(expectedUserDto, resultUserDto);
    }

    @Test
    void updateUserThrow() {
        User user = new User(1, "Вася", "vasya@example.com");
        UserDto userUpdate = new UserDto(1, "Новое имя", "newemail@example.com");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.emailIsExist("newemail@example.com")).thenReturn(true);

        assertThrows(EmailConflictException.class, () -> {
            userService.updateUser(userUpdate, 1);
        });
    }

    @Test
    void deleteUser() {
        doNothing().when(userRepository).deleteById(1);
        userService.deleteUser(1);
        verify(userRepository, times(1)).deleteById(1);
    }
}