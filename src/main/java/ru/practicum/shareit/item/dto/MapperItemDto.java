package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

public class MapperItemDto {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable());
    }

    public static Item toItem(ItemDto itemDto, int idUser) {
        return new Item(itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), idUser);
    }
}
