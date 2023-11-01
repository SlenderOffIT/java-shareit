package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingStatusEnum;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoJson;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ru.practicum.shareit.booking.BookingStatusEnum.ALL;
import static ru.practicum.shareit.booking.BookingStatusEnum.APPROVED;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;

    private UserDto user;
    private ItemDto item;
    private UserDto user2;
    private ItemDto item2;
    private BookingDtoJson bookingDtoJson;
    private BookingDtoJson bookingDtoJson2;

    @BeforeEach
    public void before() {
        user = new UserDto("Вася", "asdfgh@gmail.com");
        item = new ItemDto("item", "description", true);
        user2 = new UserDto("Игорь", "dfgh@gmail.com");
        item2 = new ItemDto("item2", "description2", false);
        bookingDtoJson = new BookingDtoJson(1, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));
        bookingDtoJson2 = new BookingDtoJson(1, LocalDateTime.now().plusHours(3), LocalDateTime.now().plusHours(4));
    }

    @Test
    public void postBookings() {
        userService.postUser(user);
        UserDto userDto = userService.postUser(user2);
        itemService.postItem(item, 1);
        BookingDto bookingDto = bookingService.postBookings(2, bookingDtoJson);

        assertNotNull(bookingDto);
        assertEquals(userDto, bookingDto.getBooker());
        assertEquals(1, bookingDto.getId());
    }

    @Test
    public void getBookingById() {
        userService.postUser(user);
        userService.postUser(user2);
        itemService.postItem(item, 1);
        BookingDto bookingDto = bookingService.postBookings(2, bookingDtoJson);

        BookingDto bookingDtoGet = bookingService.getBookingById(1, 1);

        assertNotNull(bookingDtoGet);
        assertEquals(bookingDto, bookingDtoGet);
    }

    @Test
    public void getBookingAll() {
        LocalDateTime startTime = LocalDateTime.of(2023, 12, 30, 15, 46);
        LocalDateTime endTime = LocalDateTime.of(2023, 12, 30, 16, 30);

        userService.postUser(user);
        userService.postUser(user2);
        itemService.postItem(item, 1);
        bookingDtoJson.setStart(startTime);
        bookingDtoJson.setEnd(endTime);
        BookingDto bookingDto = bookingService.postBookings(2, bookingDtoJson);
        bookingService.postBookings(2, bookingDtoJson2);

        List<BookingDto> bookingDtoList = bookingService.getAllBookingsUser(BookingStatusEnum.ALL, 2, 0, 1);

        assertEquals(1, bookingDtoList.size());
        assertEquals(bookingDto, bookingDtoList.get(0));
    }

    @Test
    public void patchApproved() {
        userService.postUser(user);
        userService.postUser(user2);
        itemService.postItem(item, 1);
        bookingService.postBookings(2, bookingDtoJson);

        BookingDto bookingDto = bookingService.patchApproved(true, 1, 1);

        assertEquals(APPROVED, bookingDto.getStatus());
    }

    @Test
    public void getListAllReservationUser() {
        userService.postUser(user);
        userService.postUser(user2);
        itemService.postItem(item, 1);
        bookingService.postBookings(2, bookingDtoJson);
        bookingService.patchApproved(true, 1, 1);

        List<BookingDto> bookingDtoList = bookingService.getListAllReservationUser(1, ALL, 0, 1);

        assertEquals(1, bookingDtoList.size());
        assertEquals(APPROVED, bookingDtoList.get(0).getStatus());
    }
}