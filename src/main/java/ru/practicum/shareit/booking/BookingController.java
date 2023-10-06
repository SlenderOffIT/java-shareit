package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoJson;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private static final String SHARER_USER = "X-Sharer-User-Id";

    private final BookingService bookingService;

    @PostMapping
    public BookingDto postBookings(@RequestHeader(SHARER_USER) int idUser,
                                   @RequestBody BookingDtoJson bookingDto) {
        log.debug("Поступил запрос на создание бронирования на вещь с id {}.", bookingDto.getItemId());
        return bookingService.postBookings(idUser, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader(SHARER_USER) int idUser, @PathVariable int bookingId) {
        log.debug("Поступил запрос на просмотр бронирования с id {}.", bookingId);
        return bookingService.getBookingById(idUser, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsUser(@RequestParam(defaultValue = "ALL") BookingStatusEnum state,
                                               @RequestHeader(SHARER_USER) int idUser) {
        log.debug("Поступил запрос на просмотр всех бронирований от арендатора со статусом {}", state);
        return bookingService.getAllBookingsUser(state, idUser);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto patchApproved(@RequestHeader(SHARER_USER) Integer idUser,
                              @RequestParam Boolean approved, @PathVariable Integer bookingId) {
        log.debug("Поступил запрос на одобрение(отклонение) бронирования с id {}", bookingId);
        return bookingService.patchApproved(approved, bookingId, idUser);
    }

    @GetMapping("/owner")
    public List<BookingDto> getListAllReservationUser(@RequestHeader(SHARER_USER) int idUser,
                                                      @RequestParam(defaultValue = "ALL") BookingStatusEnum state) {
        log.debug("Поступил запрос на просмотр всех бронировании от владельца со статусом {}", state);
        return bookingService.getListAllReservationUser(idUser, state);
    }
}
