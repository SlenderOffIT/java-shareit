package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private ItemService itemService;

    @GetMapping
    public List<ItemDto> getAllItem(@RequestHeader("X-Sharer-User-Id") int idUser) {
        log.debug("Поступил запрос на просмотр всех предметов пользователя с id {}.", idUser);
        return itemService.getAllItems(idUser);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable int itemId) {
        log.debug("Поступил запрос на просмотр предмета с id {}.", itemId);
        return itemService.getItemById(itemId);
    }

    @PostMapping
    public ItemDto post(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") int idUser) {
        log.debug("Поступил запрос на добавление предмета с названием {}.", itemDto.getName());
        return itemService.postItem(itemDto, idUser);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") int idUser,
                          @PathVariable int itemId) {
        log.debug("Поступил запрос на обновление предмета с id {}.", itemId);
        return itemService.update(itemDto, itemId, idUser);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable int itemId, @RequestHeader("X-Sharer-User-Id") int userId) {
        log.debug("Поступил запрос на удаление предмета с id {}.", itemId);
        itemService.delete(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam("text") String text) {
        log.debug("Поступил запрос по поиску предметов с наличием фрагмента {}", text);
        return itemService.search(text);
    }
}
