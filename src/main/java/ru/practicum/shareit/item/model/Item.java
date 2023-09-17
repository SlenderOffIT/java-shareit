package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.model.ItemRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    private int id;
    private String name;
    private String description;
    private Boolean available;
    private Integer owner;
    private ItemRequest request;

    public Item(String name, String description, boolean available, Integer owner) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
    }
}
