package ru.practicum.shareit.item.repository;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.MapperItemDto.toItem;
import static ru.practicum.shareit.item.dto.MapperItemDto.toItemDto;

@Component
public class InMemoryItemRepositoryImpl implements ItemRepository {

    private int id = 1;
    @Getter
    private final Map<Integer, Item> storageItem = new HashMap<>();

    @Override
    public List<ItemDto> getAllItems(int idUser) {
        List<Item> items = storageItem.values().stream()
                .filter(item -> item.getOwner().equals(idUser))
                .collect(Collectors.toList());
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item: items) {
            itemsDto.add(toItemDto(item));
        }
        return itemsDto;
    }

    @Override
    public ItemDto getItemById(int idItem) {
        return toItemDto(storageItem.get(idItem));
    }

    @Override
    public ItemDto save(ItemDto itemDto, int idUser) {
        Item item = toItem(itemDto, idUser);
        item.setId(id++);
        storageItem.put(item.getId(), item);
        return toItemDto(item);
    }

    @Override
    public ItemDto update(ItemDto itemDto, int idItem, int idUser) {
        Item item = storageItem.get(idItem);
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        item.setId(idItem);
        storageItem.put(item.getId(), item);
        return toItemDto(item);
    }

    @Override
    public void delete(int idItem) {
        storageItem.remove(idItem);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        List<Item> items = storageItem.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item: items) {
            itemsDto.add(toItemDto(item));
        }
        return itemsDto;
    }
}
