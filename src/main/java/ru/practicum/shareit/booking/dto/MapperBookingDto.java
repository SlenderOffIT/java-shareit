package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.item.MapperItemDto.toItemDto;
import static ru.practicum.shareit.user.dto.MapperUserDto.toUserDto;

public class MapperBookingDto {

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(), booking.getStart(), booking.getEnd(),
                toItemDto(booking.getItem()), toUserDto(booking.getBooker()), booking.getStatus());
    }

    public static Booking toBooking(BookingDtoJson bookingDto) {
        return new Booking(bookingDto.getStart(), bookingDto.getEnd());
    }

    public static BookingDtoResponse toBookingDtoResponse(Booking booking) {
        return new BookingDtoResponse(booking.getId(), booking.getBooker().getId(), booking.getStart(), booking.getEnd());
    }

    public static List<BookingDto> mappingListBookingByTime(List<Booking> bookingList) {
        return bookingList.stream()
                .map(MapperBookingDto::toBookingDto)
                .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                .collect(Collectors.toList());
    }
}
