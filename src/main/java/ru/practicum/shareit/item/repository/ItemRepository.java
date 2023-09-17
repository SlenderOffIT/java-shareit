package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;

public interface ItemRepository {

    List<ItemDto> getAllItems(int idUser);
    ItemDto getItemById(int idItem);
    ItemDto save(ItemDto itemDto, int idUser);
    ItemDto update(ItemDto itemDto, int idItem, int idUser);
    void delete(int idItem, int idUser);
    List<ItemDto> search(String text);
    Map<Integer, Item> getStorageItem();
}
