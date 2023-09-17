package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static ru.practicum.shareit.util.Validation.validate;

@Slf4j
@AllArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    private ItemRepository itemRepository;
    private UserRepository userRepository;

    @Override
    public List<ItemDto> getAllItems(int idUser) {
        log.debug("Обрабатываем запрос на просмотр списка всех предметов пользователя с id {}.", idUser);
        return itemRepository.getAllItems(idUser);
    }

    @Override
    public ItemDto getItemById(int idItem) {
        log.debug("Обрабатываем запрос на просмотр предмета с id {}.", idItem);
        return itemRepository.getItemById(idItem);
    }

    @Override
    public ItemDto postItem(ItemDto itemDto, int idUser) {
        log.debug("Обрабатываем запрос на добавление предмета с названием {}.", itemDto.getName());
        validate(itemDto, idUser, userRepository);
        return itemRepository.save(itemDto, idUser);
    }

    @Override
    public ItemDto update(ItemDto itemDto, int idItem, int idUser) {
        log.debug("Обрабатываем запрос на редактирование предмета с id {}.", idItem);
        if (itemRepository.getStorageItem().get(idItem).getOwner() != idUser) {
            log.debug("Пользователь с id {} пытается изменить не свой предмет с id {}.", idUser, idItem);
            throw new ItemNotFoundException("Вы не являетесь владельцем данного предмета.");
        }
        return itemRepository.update(itemDto, idItem, idUser);
    }

    @Override
    public void delete(int idItem, int idUser) {
        log.debug("Обрабатываем запрос на удаление предмета с id {}.", idItem);
        itemRepository.delete(idItem, idUser);
    }

    @Override
    public List<ItemDto> search(String text) {
        log.debug("Обрабатываем запрос по поиску предметов с наличием фрагмента {}", text);
        return itemRepository.search(text);
    }
}
