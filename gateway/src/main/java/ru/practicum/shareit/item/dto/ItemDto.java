package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    private int id;
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private Boolean available;
    private Integer requestId;
//
//    public ItemDto(int id, String name, String description, boolean available) {
//        this.id = id;
//        this.name = name;
//        this.description = description;
//        this.available = available;
//    }
//
//    public ItemDto(String name, String description, Boolean available) {
//        this.name = name;
//        this.description = description;
//        this.available = available;
//    }
}
