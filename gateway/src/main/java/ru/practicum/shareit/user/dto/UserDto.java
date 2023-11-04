package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private int id;
    @NotBlank(message = "Имя пользователя не указано")
    private String name;
    @NotBlank(message = "Email пользователя не указан")
    @Email(message = "Неверный формат email")
    private String email;

    public UserDto(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
