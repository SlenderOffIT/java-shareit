package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingBadRequest;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CommentDtoJson;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.ItemDtoResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.BookingStatusEnum.APPROVED;
import static ru.practicum.shareit.booking.BookingStatusEnum.REJECTED;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    private ItemService itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private RequestRepository requestRepository;

    @BeforeEach
    public void before() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository, requestRepository);
    }

    @Test
    public void getAllItemUserGood() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        Item item = new Item("item", "description", false, user);
        item.setId(1);
        Item item1 = new Item("item1", "description1", true, user);
        item1.setId(2);
        Comment comment = new Comment(1, "comment", item, user, LocalDateTime.now());

        List<Integer> itemsId = Arrays.asList(item.getId(), item1.getId());

        when(itemRepository.findAllByOwnerId(anyInt())).thenReturn(List.of(item, item1));
        when(commentRepository.findByItemIdIn(anyList())).thenReturn(List.of(comment));

        List<ItemDtoResponse> items = itemService.getAllItems(1);

        assertEquals(2, items.size());
        assertEquals("item", items.get(0).getName());
        assertEquals("comment", items.get(0).getComments().get(0).getText());
    }

    @Test
    public void getItemByIdGood() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        User user1 = new User(2, "Петя", "qsafrsfgh@gmail.com");
        Item item = new Item("item", "description", true, user);
        item.setId(1);
        Item item1 = new Item("item1", "description1", true, user);
        item1.setId(2);
        Comment comment = new Comment(1, "comment", item, user, LocalDateTime.now());
        Booking bookingLast = new Booking(1, LocalDateTime.now().minusHours(4), LocalDateTime.now().minusHours(2), item, user1, APPROVED);
        Booking bookingNext = new Booking(2, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3), item, user1, APPROVED);

        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.findLastBookingByOwnerId(eq(1), eq(1), eq(REJECTED), any()))
                .thenReturn(Collections.singletonList(bookingLast));
        when(bookingRepository.findNextBookingByOwnerId(eq(1), eq(1), eq(REJECTED), any()))
                .thenReturn(Collections.singletonList(bookingNext));
        when(commentRepository.findByItemId(anyInt()))
                .thenReturn(Collections.singletonList(comment));

        ItemDtoResponse itemResponse = itemService.getItemById(1, 1);

        assertEquals("item", itemResponse.getName());
        assertEquals("description", itemResponse.getDescription());
        assertEquals(1, itemResponse.getLastBooking().getId());
        assertEquals(2, itemResponse.getNextBooking().getId());
    }

    @Test
    public void getItemByIdNullLastAndNextBooking() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        User user1 = new User(2, "Петя", "qsafrsfgh@gmail.com");
        Item item = new Item("item", "description", true, user);
        item.setId(1);
        Item item1 = new Item("item1", "description1", true, user);
        item1.setId(2);
        Comment comment = new Comment(1, "comment", item, user, LocalDateTime.now());

        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.findLastBookingByOwnerId(eq(1), eq(1), eq(REJECTED), any()))
                .thenReturn(List.of());
        when(bookingRepository.findNextBookingByOwnerId(eq(1), eq(1), eq(REJECTED), any()))
                .thenReturn(List.of());
        when(commentRepository.findByItemId(anyInt()))
                .thenReturn(Collections.singletonList(comment));

        ItemDtoResponse itemResponse = itemService.getItemById(1, 1);

        assertEquals("item", itemResponse.getName());
        assertEquals("description", itemResponse.getDescription());
        assertNull(itemResponse.getLastBooking());
        assertNull(itemResponse.getNextBooking());
    }

    @Test
    public void getItemByIdNullComment() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        User user1 = new User(2, "Петя", "qsafrsfgh@gmail.com");
        Item item = new Item("item", "description", true, user);
        item.setId(1);

        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.findLastBookingByOwnerId(eq(1), eq(1), eq(REJECTED), any()))
                .thenReturn(List.of());
        when(bookingRepository.findNextBookingByOwnerId(eq(1), eq(1), eq(REJECTED), any()))
                .thenReturn(List.of());
        when(commentRepository.findByItemId(anyInt()))
                .thenReturn(List.of());

        ItemDtoResponse itemResponse = itemService.getItemById(1, 1);

        assertEquals("item", itemResponse.getName());
        assertEquals("description", itemResponse.getDescription());
        assertNull(itemResponse.getLastBooking());
        assertNull(itemResponse.getNextBooking());
        assertTrue(itemResponse.getComments().isEmpty());
    }

    @Test
    public void postItemGood() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");

        ItemDto itemDto = new ItemDto(1, "item", "description", true, null);

        int userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Item item = new Item("item", "description", true, null);
        item.setId(1);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.postItem(itemDto, userId);

        assertEquals(itemDto, result);
    }

    @Test
    public void postItemRequest() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");

        ItemDto itemDto = new ItemDto(1, "item", "description", true, 1);
        ItemRequest request = new ItemRequest(1, "description", user,LocalDateTime.now());

        int userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.getReferenceById(itemDto.getRequestId())).thenReturn(request);

        Item item = new Item(1, "item", "description", true, user, request);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.postItem(itemDto, userId);

        assertEquals(itemDto, result);
    }

    @Test
    public void updateItemGood() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        ItemDto itemDto = new ItemDto(1, "item Dto", "description Dto", false);
        Item item = new Item(1, "item", "description", true, user, null);
        itemDto.setId(1);

        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto response = itemService.update(itemDto, 1, 1);

        assertEquals(response, itemDto);
    }

    @Test
    public void updateItemThrowIdItem() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        ItemDto itemDto = new ItemDto(1, "item Dto", "description Dto", false);
        Item item = new Item(1, "item", "description", true, user, null);
        itemDto.setId(1);

        when(itemRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> {
            itemService.update(itemDto, 3, 1);
        });
    }

    @Test
    public void updateItem() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        ItemDto itemDto = new ItemDto(1, "item Dto", "description Dto", false);
        Item item = new Item(1, "item", "description", true, user, null);
        itemDto.setId(1);

        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        assertThrows(ItemNotFoundException.class, () -> {
            itemService.update(itemDto, 1, 3);
        });
    }

    @Test
    public void deleteItemThrows() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        ItemDto itemDto = new ItemDto(1, "item Dto", "description Dto", false);
        itemDto.setId(1);
        Item item = new Item(1, "item", "description", true, user, null);

        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        assertThrows(ItemNotFoundException.class, () -> {
           itemService.delete(1, 2);
        });
    }

    @Test
    public void deleteItemGood() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        ItemDto itemDto = new ItemDto(1, "item Dto", "description Dto", false);
        itemDto.setId(1);
        Item item = new Item(1, "item", "description", true, user, null);

        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        doNothing().when(itemRepository).deleteById(anyInt());

        itemService.delete(1, 1);

        verify(itemRepository, times(1)).deleteById(1);
    }

    @Test
    public void searchItemIsBlank() {
        List<ItemDto> search = itemService.search("");
        assertTrue(search.isEmpty());
    }

    @Test
    public void searchItemGood() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        ItemDto itemDto = new ItemDto(1, "item", "description", true);
        itemDto.setId(1);
        Item item = new Item(1, "item", "description", true, user, null);

        when(itemRepository.search(anyString())).thenReturn(List.of(item));

        List<ItemDto> search = itemService.search("item");

        assertEquals(itemDto, search.get(0));
    }

    @Test
    public void commentItemGood() {
        LocalDateTime time = LocalDateTime.of(2023, 9, 16, 14, 30);
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        User user1 = new User(2, "Петя", "qsqwfgh@gmail.com");

        ItemDto itemDto = new ItemDto(1, "item", "description", true);
        itemDto.setId(1);
        Item item = new Item(1, "item", "description", true, user, null);
        Comment comment = new Comment(1, "comment", item, user1, time);
        CommentDtoJson commentDtoJson = new CommentDtoJson();
        commentDtoJson.setText("comment");
        CommentDto commentDto = new CommentDto(1, "comment", user1.getName(), time);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user1));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItem_IdAndStatusAndEndBefore(eq(2), eq(1), eq(APPROVED), any(LocalDateTime.class)))
                .thenReturn(true);
        when(commentRepository.save(any())).thenReturn(comment);

        CommentDto commentDtoResponse = itemService.postComment(commentDtoJson, 2, 1);

        assertEquals(commentDto, commentDtoResponse);
    }

    @Test
    public void commentItemFailUser() {
        LocalDateTime time = LocalDateTime.of(2023, 9, 16, 14, 30);
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        User user1 = new User(2, "Петя", "qsqwfgh@gmail.com");

        ItemDto itemDto = new ItemDto(1, "item", "description", true);
        itemDto.setId(1);
        Item item = new Item(1, "item", "description", true, user, null);
        CommentDtoJson commentDtoJson = new CommentDtoJson();
        commentDtoJson.setText("comment");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user1));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItem_IdAndStatusAndEndBefore(eq(3), eq(1), eq(APPROVED), any(LocalDateTime.class)))
                .thenReturn(false);

        assertThrows(BookingBadRequest.class, () -> {
            itemService.postComment(commentDtoJson, 3, 1);
        });
    }

    @Test
    public void commentItemIsEmpty() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        User user1 = new User(2, "Петя", "qsqwfgh@gmail.com");

        ItemDto itemDto = new ItemDto(1, "item", "description", true);
        itemDto.setId(1);
        Item item = new Item(1, "item", "description", true, user, null);
        CommentDtoJson commentDtoJson = new CommentDtoJson();
        commentDtoJson.setText("");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user1));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItem_IdAndStatusAndEndBefore(eq(2), eq(1), eq(APPROVED), any(LocalDateTime.class)))
                .thenReturn(true);

        assertThrows(BookingBadRequest.class, () -> {
            itemService.postComment(commentDtoJson, 2, 1);
        });
    }

    @Test
    public void postCommentItemFailUser() {
        int userId = 1;
        int itemId = 1;

        CommentDtoJson commentJson = new CommentDtoJson();
        commentJson.setText("Test comment");

        assertThrows(UserNotFoundException.class, () -> {
            itemService.postComment(commentJson, userId, itemId);
        });
    }

    @Test
    public void postCommentItemFailItem() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        int itemId = 1;

        CommentDtoJson commentJson = new CommentDtoJson();
        commentJson.setText("Test comment");
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        assertThrows(ItemNotFoundException.class, () -> {
            itemService.postComment(commentJson, 1, itemId);
        });
    }
}