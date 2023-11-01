package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoJson;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CommentDtoJson;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.ItemDtoResponse;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private BookingService bookingService;

    private UserDto user;
    private ItemDto item;
    private UserDto user2;
    private ItemDto item2;

    @BeforeEach
    public void before() {
        user = new UserDto("Вася", "asdfgh@gmail.com");
        item = new ItemDto("item", "description", true);
        user2 = new UserDto("Игорь", "dfgh@gmail.com");
        item2 = new ItemDto("item2", "description2", false);
    }

    @Test
    public void postItem() {
        userService.postUser(user);
        ItemDto itemDto = itemService.postItem(item, 1);

        assertEquals(1, itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
    }

    @Test
    public void getItemById() {
        userService.postUser(user);
        itemService.postItem(item, 1);

        ItemDtoResponse responseItem = itemService.getItemById(1, 1);

        assertNotNull(responseItem);
        assertEquals(1, responseItem.getId());
        assertEquals("item", responseItem.getName());
        assertEquals(true, responseItem.getAvailable());
    }

    @Test
    public void getAllItems() {
        userService.postUser(user);
        itemService.postItem(item, 1);
        userService.postUser(user2);
        itemService.postItem(item2, 2);

        List<ItemDtoResponse> listAllItems = itemService.getAllItems(1);

        assertEquals(1, listAllItems.size());
        assertEquals(1, listAllItems.get(0).getId());

        listAllItems = itemService.getAllItems(2);

        assertEquals(1, listAllItems.size());
        assertEquals(2, listAllItems.get(0).getId());
    }

    @Test
    public void updateItem() {
        userService.postUser(user);
        itemService.postItem(item, 1);

        ItemDto itemUpdate = itemService.update(item2, 1, 1);

        assertEquals("item2", itemUpdate.getName());
        assertEquals(1, itemUpdate.getId());
        assertEquals(false, itemUpdate.getAvailable());
    }

    @Test
    public void deleteItem() {
        userService.postUser(user);
        itemService.postItem(item, 1);

        itemService.delete(1, 1);

        assertThrows(ItemNotFoundException.class, () -> {
           itemService.getItemById(1, 1);
        });
    }

    @Test
    public void search() {
        userService.postUser(user);
        ItemDto itemDto = itemService.postItem(item, 1);
        itemService.postItem(item2, 1);

        List<ItemDto> listSearch = itemService.search("item");

        assertEquals(1, listSearch.size());
        assertEquals(itemDto, listSearch.get(0));
    }

    @Test
    public void commentItem() {
        userService.postUser(user);
        userService.postUser(user2);
        ItemDto itemDto = itemService.postItem(item, 1);

        BookingDtoJson booking = new BookingDtoJson(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDto = bookingService.postBookings(2, booking);
        bookingService.patchApproved(true, 1, 1);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        CommentDtoJson commentDtoJson = new CommentDtoJson("comment", 1);
        CommentDto commentDto = itemService.postComment(commentDtoJson, 2, 1);

        assertEquals("comment", commentDto.getText());
        assertEquals(1, commentDto.getId());
    }
}