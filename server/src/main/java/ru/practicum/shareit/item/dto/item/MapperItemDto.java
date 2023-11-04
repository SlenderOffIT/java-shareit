package ru.practicum.shareit.item.dto.item;

import ru.practicum.shareit.item.model.Item;

public class MapperItemDto {
    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getIsAvailable());
        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }
//        } else {
//            itemDto.setRequestId(null);
//        }
        return itemDto;
    }

    public static ItemDtoResponse toItemResponse(Item item) {
        return new ItemDtoResponse(item.getId(), item.getName(), item.getDescription(), item.getIsAvailable());
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());
    }
}
