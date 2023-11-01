package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoJson;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    private static final String SHARER_USER = "X-Sharer-User-Id";

    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mvc;

    @Test
    public void postBooking() throws Exception {
        int userId = 2;
        ItemDto itemDto = new ItemDto(1, "item", "description", true);
        UserDto userDto = new UserDto(1, "Вася", "asdfgh@gmail.com");
        BookingDtoJson bookingDtoJson = new BookingDtoJson(1, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));

        BookingDto bookingDto = new BookingDto(1, bookingDtoJson.getStart(), bookingDtoJson.getEnd(), itemDto, userDto, BookingStatusEnum.WAITING);

        when(bookingService.postBookings(anyInt(), eq(bookingDtoJson))).thenReturn(bookingDto);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoJson))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class));
    }

    @Test
    public void getBookingById() throws Exception {
        int userId = 1;
        int bookingId = 1;
        ItemDto itemDto = new ItemDto(1, "item", "description", true);
        UserDto userDto = new UserDto(1, "Вася", "asdfgh@gmail.com");
        BookingDto bookingDto = new BookingDto(1, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2), itemDto, userDto, BookingStatusEnum.WAITING);

        when(bookingService.getBookingById(1, 1)).thenReturn(bookingDto);

        mvc.perform(get("/bookings/{id}", bookingId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER, userId))
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.item.id", is(1)))
                .andExpect(jsonPath("$.item.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.item.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.item.available", is(true)))
                .andExpect(jsonPath("$.item.requestId").doesNotExist())
                .andExpect(jsonPath("$.booker.id", is(1)))
                .andExpect(jsonPath("$.booker.name", is(userDto.getName())))
                .andExpect(jsonPath("$.booker.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    public void getAllBooking() throws Exception {
        int userId = 1;
        int from = 0;
        int size = 10;
        BookingStatusEnum state = BookingStatusEnum.ALL;

        ItemDto itemDto = new ItemDto(1, "item", "description", true);
        UserDto userDto = new UserDto(1, "Вася", "asdfgh@gmail.com");
        BookingDto bookingDto = new BookingDto(1, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2), itemDto, userDto, BookingStatusEnum.WAITING);
        BookingDto bookingDto2 = new BookingDto(2, LocalDateTime.now().plusHours(3), LocalDateTime.now().plusHours(5), itemDto, userDto, BookingStatusEnum.WAITING);
        List<BookingDto> bookingDtoList = Arrays.asList(bookingDto, bookingDto2);

        when(bookingService.getAllBookingsUser(state, userId, from, size)).thenReturn(bookingDtoList);

        mvc.perform(get("/bookings")
                        .param("state", String.valueOf(state))
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER, userId))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1), Integer.class))
                .andExpect(jsonPath("$[0].item.id", is(1)))
                .andExpect(jsonPath("$[0].item.name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].item.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].item.available", is(true)))
                .andExpect(jsonPath("$[0].item.requestId").doesNotExist())
                .andExpect(jsonPath("$[0].booker.id", is(1)))
                .andExpect(jsonPath("$[0].booker.name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].booker.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$[1].id", is(2), Integer.class))
                .andExpect(jsonPath("$[1].item.id", is(1)))
                .andExpect(jsonPath("$[1].item.name", is(itemDto.getName())))
                .andExpect(jsonPath("$[1].item.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[1].item.available", is(true)))
                .andExpect(jsonPath("$[1].item.requestId").doesNotExist())
                .andExpect(jsonPath("$[1].booker.id", is(1)))
                .andExpect(jsonPath("$[1].booker.name", is(userDto.getName())))
                .andExpect(jsonPath("$[1].booker.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$[1].status", is(bookingDto2.getStatus().toString())));
    }

    @Test
    public void patchApproved() throws Exception {
        boolean approved = true;
        int userId = 1;
        int bookingId = 1;
        ItemDto itemDto = new ItemDto(1, "item", "description", true);
        UserDto userDto = new UserDto(1, "Вася", "asdfgh@gmail.com");
        BookingDto bookingDto = new BookingDto(1, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2), itemDto, userDto, BookingStatusEnum.APPROVED);

        when(bookingService.patchApproved(approved, bookingId, userId)).thenReturn(bookingDto);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", String.valueOf(approved))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER, userId))
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.item.id", is(1)))
                .andExpect(jsonPath("$.item.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.item.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.item.available", is(true)))
                .andExpect(jsonPath("$.item.requestId").doesNotExist())
                .andExpect(jsonPath("$.booker.id", is(1)))
                .andExpect(jsonPath("$.booker.name", is(userDto.getName())))
                .andExpect(jsonPath("$.booker.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    public void getListAllReservationUser() throws Exception {
        int userId = 1;
        int from = 0;
        int size = 10;
        BookingStatusEnum state = BookingStatusEnum.ALL;

        ItemDto itemDto = new ItemDto(1, "item", "description", true);
        UserDto userDto = new UserDto(1, "Вася", "asdfgh@gmail.com");
        BookingDto bookingDto = new BookingDto(1, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2), itemDto, userDto, BookingStatusEnum.WAITING);
        BookingDto bookingDto2 = new BookingDto(2, LocalDateTime.now().plusHours(3), LocalDateTime.now().plusHours(5), itemDto, userDto, BookingStatusEnum.WAITING);
        List<BookingDto> bookingDtoList = Arrays.asList(bookingDto, bookingDto2);

        when(bookingService.getListAllReservationUser(userId, state, from, size)).thenReturn(bookingDtoList);

        mvc.perform(get("/bookings/owner")
                        .param("state", String.valueOf(state))
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER, userId))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1), Integer.class))
                .andExpect(jsonPath("$[0].item.id", is(1)))
                .andExpect(jsonPath("$[0].item.name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].item.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].item.available", is(true)))
                .andExpect(jsonPath("$[0].item.requestId").doesNotExist())
                .andExpect(jsonPath("$[0].booker.id", is(1)))
                .andExpect(jsonPath("$[0].booker.name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].booker.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$[1].id", is(2), Integer.class))
                .andExpect(jsonPath("$[1].item.id", is(1)))
                .andExpect(jsonPath("$[1].item.name", is(itemDto.getName())))
                .andExpect(jsonPath("$[1].item.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[1].item.available", is(true)))
                .andExpect(jsonPath("$[1].item.requestId").doesNotExist())
                .andExpect(jsonPath("$[1].booker.id", is(1)))
                .andExpect(jsonPath("$[1].booker.name", is(userDto.getName())))
                .andExpect(jsonPath("$[1].booker.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$[1].status", is(bookingDto2.getStatus().toString())));
    }
}