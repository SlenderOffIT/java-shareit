package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestJson;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.service.RequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    private static final String SHARER_USER = "X-Sharer-User-Id";

    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private RequestService requestService;
    @Autowired
    private MockMvc mvc;

    @Test
    public void postRequest() throws Exception {
        int userId = 1;
        ItemRequestJson itemRequestJson = new ItemRequestJson("Дрель ручная");
        ItemRequestDto itemRequestDto = new ItemRequestDto(1, itemRequestJson.getDescription(), userId, LocalDateTime.now());

        when(requestService.createRequest(anyInt(), any(ItemRequestJson.class))).thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestJson))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class));
    }

    @Test
    public void getRequestById() throws Exception {
        int userId = 1;
        int requestId = 1;
        ItemRequestResponse itemRequestDto = new ItemRequestResponse(1, "Дрель ручная", userId, LocalDateTime.now(), new ArrayList<>());

        when(requestService.getRequestById(anyInt(), anyInt())).thenReturn(itemRequestDto);
        mvc.perform(get("/requests/{requestId}", requestId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }

    @Test
    public void getAllRequest() throws Exception {
        int userId = 1;
        int from = 0;
        int size = 10;
        ItemRequestResponse itemRequestDto = new ItemRequestResponse(1, "Дрель ручная", userId, LocalDateTime.now(), new ArrayList<>());
        ItemRequestResponse itemRequestDto2 = new ItemRequestResponse(2, "Пила ручная", userId, LocalDateTime.now(), new ArrayList<>());
        List<ItemRequestResponse> itemRequestResponseList = Arrays.asList(itemRequestDto, itemRequestDto2);

        when(requestService.getAllRequest(anyInt(), anyInt(), anyInt())).thenReturn(itemRequestResponseList);
        mvc.perform(get("/requests/all")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1), Integer.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[1].id", is(2), Integer.class))
                .andExpect(jsonPath("$[1].description", is(itemRequestDto2.getDescription())));
    }

    @Test
    public void getAllRequestByUser() throws Exception {
        int userId = 1;
        ItemRequestResponse itemRequestDto = new ItemRequestResponse(1, "Дрель ручная", userId, LocalDateTime.now(), new ArrayList<>());
        ItemRequestResponse itemRequestDto2 = new ItemRequestResponse(2, "Пила ручная", userId, LocalDateTime.now(), new ArrayList<>());
        List<ItemRequestResponse> itemRequestResponseList = Arrays.asList(itemRequestDto, itemRequestDto2);

        when(requestService.getAllRequestByUser(userId)).thenReturn(itemRequestResponseList);
        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1), Integer.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[1].id", is(2), Integer.class))
                .andExpect(jsonPath("$[1].description", is(itemRequestDto2.getDescription())));
    }
}