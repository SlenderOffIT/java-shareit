package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    List<ItemDto> getAllItems(int idUser);

    ItemDto getItemById(int idItem);

    ItemDto postItem(ItemDto itemDto, int idUser);

    ItemDto update(ItemDto itemDto, int idItem, int idUser);

    void delete(int idItem, int idUser);

    List<ItemDto> search(String text);
}
