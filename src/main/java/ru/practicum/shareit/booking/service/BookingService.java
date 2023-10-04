package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingStatusEnum;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoJson;

import java.util.List;

public interface BookingService {

    BookingDto postBookings(int idUser, BookingDtoJson bookingDto);

    BookingDto getBookingById(int idUser, int bookingId);

    List<BookingDto> getAllBookingsUser(BookingStatusEnum state, int idUser);

    BookingDto patchApproved(Boolean approved, Integer bookingId, Integer idUser);

    List<BookingDto> getListAllReservationUser(int idUser, BookingStatusEnum state);
}
