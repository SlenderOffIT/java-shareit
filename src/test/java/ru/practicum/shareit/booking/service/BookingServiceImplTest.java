package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoJson;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.BookingStatusEnum.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    private BookingService bookingService;

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void before() {
        bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository);
    }

    @Test
    public void postBookingNullItem() {
        UserDto user = new UserDto(1, "Вася", "asdfgh@gmail.com");
        ItemDto item = new ItemDto(1, "item", "description", true, user.getId());
        item.setId(1);
        BookingDtoJson bookingDtoJson = new BookingDtoJson(1, 1, LocalDateTime.now().plusMinutes(12), LocalDateTime.now().plusHours(1));

        assertThrows(ItemNotFoundException.class, () -> {
            bookingService.postBookings(1, bookingDtoJson);
        });
    }

    @Test
    public void postBookingMineItem() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        Item item = new Item( "item", "description", true, user);
        item.setId(1);
        BookingDtoJson bookingDtoJson = new BookingDtoJson(1, 1, LocalDateTime.now().plusMinutes(12), LocalDateTime.now().plusHours(1));

        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        assertThrows(BookingNotFoundException.class, () -> {
            bookingService.postBookings(1, bookingDtoJson);
        });
    }

    @Test
    public void postBookingNotIsAvailable() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        Item item = new Item( "item", "description", false, user);
        item.setId(1);
        BookingDtoJson bookingDtoJson = new BookingDtoJson(1, 1, LocalDateTime.now().plusMinutes(12), LocalDateTime.now().plusHours(1));

        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        assertThrows(BookingBadRequest.class, () -> {
            bookingService.postBookings(2, bookingDtoJson);
        });
    }

    @Test
    public void postBookingNotUser() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        Item item = new Item( "item", "description", true, user);
        item.setId(1);
        BookingDtoJson bookingDtoJson = new BookingDtoJson(1, 1, LocalDateTime.now().plusMinutes(12), LocalDateTime.now().plusHours(1));

        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            bookingService.postBookings(2, bookingDtoJson);
        });
    }

    @Test
    public void postBookingGood() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        User user1 = new User(2, "Петя", "qsqwfgh@gmail.com");
        Item item = new Item( "item", "description", true, user);
        item.setId(1);
        BookingDtoJson bookingDtoJson = new BookingDtoJson(1, 1, LocalDateTime.now().plusMinutes(12), LocalDateTime.now().plusHours(1));
        Booking booking = new Booking(1, LocalDateTime.now().plusMinutes(12), LocalDateTime.now().plusHours(1), item, user, WAITING);

        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user1));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto bookingDto = bookingService.postBookings(2, bookingDtoJson);

        assertEquals(WAITING, bookingDto.getStatus());
        assertEquals("item", bookingDto.getItem().getName());
    }

    @Test
    public void getBookingByIdNotBooking() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> {
            bookingService.getBookingById(2, 1);
        });
    }

    @Test
    public void getBookingByIdGood() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        Item item = new Item( "item", "description", true, user);
        item.setId(1);
        Booking booking = new Booking(1, LocalDateTime.now().plusMinutes(12), LocalDateTime.now().plusHours(1), item, user, WAITING);

        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        BookingDto bookingDto = bookingService.getBookingById(1, 1);

        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
    }

    @Test
    public void getBookingByIdNotOwner() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        Item item = new Item( "item", "description", true, user);
        item.setId(1);
        Booking booking = new Booking(1, LocalDateTime.now().plusMinutes(12), LocalDateTime.now().plusHours(1), item, user, WAITING);

        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        assertThrows(UserNotFoundException.class, () -> {
            bookingService.getBookingById(3, 1);
        });
    }

    @Test
    public void getAllBookingsUserFailPageable() {
        assertThrows(ValidationException.class, () -> {
            bookingService.getAllBookingsUser(WAITING, 1, -1, -1);
        });
    }

    @Test
    public void getAllBookingsNotUser() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> {
            bookingService.getAllBookingsUser(WAITING, 1, 0, 2);
        });
    }

    @Test
    public void getAllBookingStatusAll() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        Item item = new Item("item", "description", true, user);
        Booking booking = new Booking(1, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2), item, user, APPROVED);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerId(anyInt(), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDto> bookingDto = bookingService.getAllBookingsUser(ALL, 1, 0, 1);

        assertEquals(1, bookingDto.size());
        assertEquals("item", bookingDto.get(0).getItem().getName());
    }

    @Test
    public void getAllBookingStatusPast() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        Item item = new Item("item", "description", true, user);
        Booking booking = new Booking(1, LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), item, user, APPROVED);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findByEndIsBeforeAndBookerId(any(), anyInt(), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDto> bookingDto = bookingService.getAllBookingsUser(PAST, 1, 0, 1);

        assertEquals(1, bookingDto.size());
        assertEquals("item", bookingDto.get(0).getItem().getName());
    }

    @Test
    public void getAllBookingStatusFuture() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        Item item = new Item("item", "description", true, user);
        Booking booking = new Booking(1, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3), item, user, APPROVED);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findByStartIsAfterAndBookerId(any(), anyInt(), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDto> bookingDto = bookingService.getAllBookingsUser(FUTURE, 1, 0, 1);

        assertEquals(1, bookingDto.size());
        assertEquals("item", bookingDto.get(0).getItem().getName());
    }

    @Test
    public void getAllBookingStatusCurrent() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        Item item = new Item("item", "description", true, user);
        Booking booking = new Booking(1, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3), item, user, APPROVED);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(anyInt(), any(), any(), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDto> bookingDto = bookingService.getAllBookingsUser(CURRENT, 1, 0, 1);

        assertEquals(1, bookingDto.size());
        assertEquals("item", bookingDto.get(0).getItem().getName());
    }

    @Test
    public void getAllBookingStatusWating() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        Item item = new Item("item", "description", true, user);
        Booking booking = new Booking(1, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3), item, user, WAITING);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findByStatusAndBookerId(eq(WAITING), anyInt(), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDto> bookingDto = bookingService.getAllBookingsUser(WAITING, 1, 0, 1);

        assertEquals(1, bookingDto.size());
        assertEquals("item", bookingDto.get(0).getItem().getName());
    }

    @Test
    public void getAllBookingStatusUnsupported() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        assertThrows(BookingBadRequest.class, () -> {
            bookingService.getAllBookingsUser(UNSUPPORTED_STATUS, 1, 0, 1);
        });
    }

    @Test
    public void patchApprovedFailBooking() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(BookingNotFoundException.class, () -> {
           bookingService.patchApproved(true, 1, 1);
        });
    }

    @Test
    public void patchApprovedInApproved() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        Item item = new Item("item", "description", true, user);
        Booking booking = new Booking(1, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3), item, user, APPROVED);

        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        assertThrows(BookingBadRequest.class, () -> {
            bookingService.patchApproved(true, 1, 1);
        });
    }

    @Test
    public void patchApprovedBusy() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        Item item = new Item("item", "description", true, user);
        item.setId(1);
        Booking booking = new Booking(1, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3), item, user, WAITING);

        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        when(bookingRepository.existsByItem_IdAndStartBeforeAndEndAfter(eq(1), any(), any())).thenReturn(true);

        assertThrows(BookingNotFoundException.class, () -> {
            bookingService.patchApproved(true, 1, 1);
        });
    }

    @Test
    public void patchApprovedNotYours() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        Item item = new Item("item", "description", true, user);
        item.setId(1);
        Booking booking = new Booking(1, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3), item, user, WAITING);

        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        when(bookingRepository.existsByItem_IdAndStartBeforeAndEndAfter(eq(1), any(), any())).thenReturn(false);

        assertThrows(BookingNotFoundException.class, () -> {
            bookingService.patchApproved(true, 1, 2);
        });
    }

    @Test
    public void patchApprovedTrue() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        Item item = new Item("item", "description", true, user);
        item.setId(1);
        Booking booking = new Booking(1, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3), item, user, WAITING);

        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        when(bookingRepository.existsByItem_IdAndStartBeforeAndEndAfter(eq(1), any(), any())).thenReturn(false);
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto bookingDto = bookingService.patchApproved(true, 1, 1);

        assertNotNull(bookingDto);
        assertEquals(APPROVED, bookingDto.getStatus());
    }

    @Test
    public void patchApprovedFalse() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        Item item = new Item("item", "description", true, user);
        item.setId(1);
        Booking booking = new Booking(1, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3), item, user, WAITING);

        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        when(bookingRepository.existsByItem_IdAndStartBeforeAndEndAfter(eq(1), any(), any())).thenReturn(false);
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto bookingDto = bookingService.patchApproved(false, 1, 1);

        assertNotNull(bookingDto);
        assertEquals(REJECTED, bookingDto.getStatus());
    }

    @Test
    public void getListAllReservationUserFailPageable() {
        assertThrows(ValidationException.class, () -> {
            bookingService.getListAllReservationUser(1, WAITING, -1, -1);
        });
    }

    @Test
    public void getListAllReservationUserNotUser() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> {
            bookingService.getListAllReservationUser(1, WAITING, 0, 2);
        });
    }

    @Test
    public void getListAllReservationUserStatusAll() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        Item item = new Item("item", "description", true, user);
        Booking booking = new Booking(1, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2), item, user, APPROVED);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllBookingByOwnerId(anyInt(), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDto> bookingDto = bookingService.getListAllReservationUser(1, ALL, 0, 1);

        assertEquals(1, bookingDto.size());
        assertEquals("item", bookingDto.get(0).getItem().getName());
    }

    @Test
    public void getListAllReservationUserStatusPast() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        Item item = new Item("item", "description", true, user);
        Booking booking = new Booking(1, LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), item, user, APPROVED);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findByItem_Owner_IdAndEndIsBefore(anyInt(), any(), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDto> bookingDto = bookingService.getListAllReservationUser(1, PAST, 0, 1);

        assertEquals(1, bookingDto.size());
        assertEquals("item", bookingDto.get(0).getItem().getName());
    }

    @Test
    public void getListAllReservationUserStatusFuture() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        Item item = new Item("item", "description", true, user);
        Booking booking = new Booking(1, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3), item, user, APPROVED);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findByItem_Owner_IdAndEndIsAfter(anyInt(), any(), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDto> bookingDto = bookingService.getListAllReservationUser(1, FUTURE, 0, 1);

        assertEquals(1, bookingDto.size());
        assertEquals("item", bookingDto.get(0).getItem().getName());
    }

    @Test
    public void getListAllReservationUserStatusCurrent() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        Item item = new Item("item", "description", true, user);
        Booking booking = new Booking(1, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3), item, user, APPROVED);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findByItem_Owner_IdAndStartBeforeAndEndAfter(anyInt(), any(), any(), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDto> bookingDto = bookingService.getListAllReservationUser(1, CURRENT, 0, 1);

        assertEquals(1, bookingDto.size());
        assertEquals("item", bookingDto.get(0).getItem().getName());
    }

    @Test
    public void getListAllReservationUserStatusWating() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        Item item = new Item("item", "description", true, user);
        Booking booking = new Booking(1, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3), item, user, WAITING);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findByItem_Owner_IdAndStatus(anyInt(), eq(WAITING), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDto> bookingDto = bookingService.getListAllReservationUser(1, WAITING, 0, 1);

        assertEquals(1, bookingDto.size());
        assertEquals("item", bookingDto.get(0).getItem().getName());
    }

    @Test
    public void getListAllReservationUserUnsupported() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        assertThrows(BookingBadRequest.class, () -> {
            bookingService.getListAllReservationUser(1, UNSUPPORTED_STATUS, 0, 1);
        });
    }
}