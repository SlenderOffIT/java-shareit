package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class UserDto {

    private int id;
    @NotNull
    private String name;
    @NotNull
    private String email;
}
