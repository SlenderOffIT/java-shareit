package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDtoJson;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private static final String SHARER_USER = "X-Sharer-User-Id";

    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAllItemsUser(@RequestHeader(SHARER_USER) long userId) {
        log.info("Get items user {}", userId);
        return itemClient.getAllItemsUser(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(SHARER_USER) long idUser, @PathVariable long itemId) {
        log.info("Get item {} user {}", itemId, idUser);
        return itemClient.getItemById(idUser, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> postItem(@RequestBody @Valid ItemDto itemDto, @RequestHeader(SHARER_USER) int idUser) {
        log.info("Post item {} user {}", itemDto, idUser);
        return itemClient.postItem(itemDto, idUser);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patchItem(@RequestBody ItemDto itemDto,
                                            @RequestHeader(SHARER_USER) long idUser,
                                            @PathVariable long itemId) {
        log.info("Patch item {} user {}", itemId, idUser);
        return itemClient.patchItem(itemDto, idUser, itemId);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@PathVariable int itemId, @RequestHeader(SHARER_USER) int userId) {
        log.info("Delete item {}, user {}", itemId, userId);
        return itemClient.deleteItem(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam("text") String text) {
        log.info("Search text {}", text);
        return itemClient.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@RequestBody @Valid CommentDtoJson comment,
                                              @RequestHeader(SHARER_USER) long idUser,
                                              @PathVariable long itemId) {
        log.info("Post comment item {}, user {}", itemId, idUser);
        return itemClient.postComment(comment, idUser, itemId);
    }
}
