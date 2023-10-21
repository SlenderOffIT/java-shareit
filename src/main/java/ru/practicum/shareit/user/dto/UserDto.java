package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private int id;
    @NotNull
    private String name;
    @NotNull
    private String email;

    public UserDto(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
