package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.model.ItemRequest;

public class MapperRequestDto {

    public static ItemRequest toRequest(ItemRequestJson itemRequestJson) {
        return new ItemRequest(itemRequestJson.getDescription());
    }

    public static ItemRequestDto toRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(),
                itemRequest.getRequester().getId(), itemRequest.getCreated());
    }

    public static ItemRequestResponse toRequestResponse(ItemRequest itemRequest) {
        return new ItemRequestResponse(itemRequest.getId(), itemRequest.getDescription(),
                itemRequest.getRequester().getId(), itemRequest.getCreated());
    }
}
