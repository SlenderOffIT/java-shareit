package ru.practicum.shareit.item.dto.item;

import ru.practicum.shareit.item.model.Item;

public class MapperItemDto {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getIsAvailable());
    }

    public static ItemDtoResponse toItemResponse(Item item) {
        return new ItemDtoResponse(item.getId(), item.getName(), item.getDescription(), item.getIsAvailable());
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());
    }
}
