package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CommentDtoJson;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    private static final String SHARER_USER = "X-Sharer-User-Id";

    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemService itemService;
    @Autowired
    private MockMvc mvc;

    @Test
    public void postItem() throws Exception {
        int userId = 1;
        ItemDto itemDto = new ItemDto("item", "description", true);

        when(itemService.postItem(itemDto, userId)).thenAnswer(invocationOnMock -> {
            ItemDto item = invocationOnMock.getArgument(0, ItemDto.class);
            item.setId(1);
            return item;
        });
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId())));
    }

    @Test
    public void getItemById() throws Exception {
        int userId = 1;
        int itemId = 1;
        ItemDtoResponse itemDto = new ItemDtoResponse(1, "item", "description",
                true, null, null, null, new ArrayList<>());

        when(itemService.getItemById(1, 1)).thenReturn(itemDto);
        mvc.perform(get("/items/{id}", itemId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.request", is(itemDto.getRequest())))
                .andExpect(jsonPath("$.lastBooking", is(itemDto.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking", is(itemDto.getNextBooking())))
                .andExpect(jsonPath("$.comments", is(itemDto.getComments())));
    }

    @Test
    public void getAllItems() throws Exception {
        int userId = 1;
        ItemDtoResponse itemDto = new ItemDtoResponse(1, "item", "description",
                true, null, null, null, new ArrayList<>());
        ItemDtoResponse itemDto2 = new ItemDtoResponse(2, "item2", "description2",
                true, null, null, null, new ArrayList<>());
        List<ItemDtoResponse> responses = Arrays.asList(itemDto, itemDto2);

        when(itemService.getAllItems(userId)).thenReturn(responses);
        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1), Integer.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$[0].request", is(itemDto.getRequest())))
                .andExpect(jsonPath("$[0].lastBooking", is(itemDto.getLastBooking())))
                .andExpect(jsonPath("$[0].nextBooking", is(itemDto.getNextBooking())))
                .andExpect(jsonPath("$[0].comments", is(itemDto.getComments())))
                .andExpect(jsonPath("$[1].id", is(2), Integer.class))
                .andExpect(jsonPath("$[1].name", is(itemDto2.getName())))
                .andExpect(jsonPath("$[1].description", is(itemDto2.getDescription())))
                .andExpect(jsonPath("$[1].available", is(itemDto2.getAvailable())))
                .andExpect(jsonPath("$[1].request", is(itemDto2.getRequest())))
                .andExpect(jsonPath("$[1].lastBooking", is(itemDto2.getLastBooking())))
                .andExpect(jsonPath("$[1].nextBooking", is(itemDto2.getNextBooking())))
                .andExpect(jsonPath("$[1].comments", is(itemDto2.getComments())));
    }

    @Test
    public void updateItem() throws Exception {
        int userId = 1;
        int itemId = 1;
        ItemDto itemDto = new ItemDto(1, "item", "description", true);

        when(itemService.update(itemDto,itemId, userId)).thenReturn(itemDto);
        mvc.perform(patch("/items/{id}", itemId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId())));
    }

    @Test
    public void deleteItem() throws Exception {
        int userId = 1;
        int itemId = 1;

        doNothing().when(itemService).delete(itemId, userId);
        mvc.perform(delete("/items/{id}", itemId)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(SHARER_USER, 1));

        verify(itemService, times(1)).delete(itemId,userId);
    }

    @Test
    public void search() throws Exception {
        String text = "2";
        ItemDto itemDto = new ItemDto(1, "item", "description", true, null);
        ItemDto itemDto2 = new ItemDto(2, "item2", "description2", true, null);
        List<ItemDto> responses = List.of(itemDto2);

        when(itemService.search(text)).thenReturn(responses);
        mvc.perform(get("/items/search")
                        .param("text", text)
                        .content(mapper.writeValueAsString(text))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(2), Integer.class))
                .andExpect(jsonPath("$[0].name", is(itemDto2.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto2.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto2.getAvailable())))
                .andExpect(jsonPath("$[0].requestId", is(itemDto2.getRequestId())));
    }

    @Test
    public void postComment() throws Exception {
        int userId = 2;
        int itemId = 1;
        UserDto userDto = new UserDto(1, "Вася", "asdfgh@gmail.com");
        ItemDto itemDto = new ItemDto(1, "item", "description", true, null);
        CommentDtoJson commentDtoJson = new CommentDtoJson("comment", 1);

        when(itemService.postComment(commentDtoJson, userId, itemId)).thenAnswer(invocationOnMock -> {
            CommentDto commentDto = new CommentDto(1, "comment", userDto.getName(), LocalDateTime.now());
            return commentDto;
        });
        mvc.perform(post("/items/{itemId}/comment", itemId)
                    .content(mapper.writeValueAsString(commentDtoJson))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header(SHARER_USER, 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.text", is(commentDtoJson.getText())));
    }
}