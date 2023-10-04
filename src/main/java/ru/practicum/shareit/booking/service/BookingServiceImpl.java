package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatusEnum;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoJson;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.booking.dto.MapperBookingDto.*;
import static ru.practicum.shareit.util.Validation.validate;

@Service
@Slf4j
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private BookingRepository bookingRepository;
    private ItemRepository itemRepository;
    private UserRepository userRepository;

    @Override
    public BookingDto postBookings(int idUser, BookingDtoJson bookingDto) {
        log.debug("Обрабатываем запрос на добавление бронирования на предмет с id {}.", bookingDto.getItemId());

        validate(bookingDto);
        Booking book = toBooking(bookingDto);

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> {
                    log.debug("Пользователь с id {} запросил аренду несуществующего предмета с id {}.", idUser, bookingDto.getItemId());
                    return new ItemNotFoundException(String.format("Предмета с id %d не существует", bookingDto.getItemId()));
                });

        if (item.getOwner().getId() == idUser) {
            log.debug("Пользователь с id {} пытается создать бронирование на свой предмет с id {}.", idUser, item.getId());
            throw new BookingNotFoundException("Вы не можете арендовать свой предмет");
        }

        if (!item.getIsAvailable()) {
            log.debug("Предмет с id {} не доступен для аренды", item.getId());
            throw new BookingBadRequest(String.format("%s не доступен для аренды.", item.getName()));
        }
        book.setItem(item);

        userRepository.findById(idUser)
                .orElseThrow(() -> {
                    log.debug("Пользователя с таким id {} не существует.", idUser);
                    return new UserNotFoundException(String.format("Пользователя с таким id %d не существует.", idUser));
                });
        User user = userRepository.getReferenceById(idUser);

        book.setBooker(user);
        book.setStatus(BookingStatusEnum.WAITING);
        return toBookingDto(bookingRepository.save(book));
    }

    @Override
    public BookingDto getBookingById(int idUser, int bookingId) {
        log.debug("Обрабатываем запрос на просмотр бронирования с id {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.debug("Бронирования с id {} не существует.", bookingId);
                    return new BookingNotFoundException(String.format("Бронирования с id %d не существует.", bookingId));
                });

        if (booking.getBooker().getId() == idUser || booking.getItem().getOwner().getId() == idUser) {
            return toBookingDto(booking);
        } else {
            log.debug("Пользователь с id {} пытается посмотреть не свой предмет или бронирование с id {}.", idUser, bookingId);
            throw new UserNotFoundException(String.format("Вы не являетесь владельцем вещи или создателем бронирования с id %d.", bookingId));
        }
    }

    @Override
    public List<BookingDto> getAllBookingsUser(BookingStatusEnum state, int idUser) {
        log.debug("Обрабатываем запрос на просмотр всех бронирований от арендатора со статусом {}", state);

        LocalDateTime localDateTime = LocalDateTime.now();

        userRepository.findById(idUser)
                .orElseThrow(() -> {
                    log.debug("Пользователя с таким id {} не существует.", idUser);
                    return new UserNotFoundException(String.format("Пользователя с таким id %d не существует.", idUser));
                });

        switch (state) {
            case ALL:
                log.debug("Выводим список бронирований со статусом бронирования {}.", state);
                return mappingListBookingByTime(bookingRepository.findByBookerId(idUser));
            case PAST:
                log.debug("Выводим список бронирований со статусом бронирования {}.", state);
                return mappingListBookingByTime(bookingRepository.findByEndIsBeforeAndBookerId(localDateTime, idUser));
            case FUTURE:
                log.debug("Выводим список бронирований со статусом бронирования {}.", state);
                return mappingListBookingByTime(bookingRepository.findByStartIsAfterAndBookerId(localDateTime, idUser));
            case CURRENT:
                log.debug("Выводим список бронирований со статусом бронирования {}.", state);
                return mappingListBookingByTime(bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(idUser, localDateTime, localDateTime));
            case WAITING:
            case REJECTED:
                log.debug("Выводим список бронирований со статусом бронирования {}.", state);
                return mappingListBookingByTime(bookingRepository.findByStatusAndBookerId(state, idUser));
        }
        log.debug("Не верный статус бронирования {}", state);
        throw new BookingBadRequest(String.format("Unknown state: %s", state));
    }

    @Override
    public BookingDto patchApproved(Boolean approved, Integer bookingId, Integer idUser) {
        log.debug("Обрабатываем запрос на одобрение(отклонение) бронирования с id {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.debug("Бронирования с id {} не существует.", bookingId);
                    return new BookingNotFoundException(String.format("Бронирования с id %d не существует.", bookingId));
                });

        Item item = booking.getItem();

        if (booking.getStatus().equals(BookingStatusEnum.APPROVED)) {
            log.debug("Подтверждение на бронирование с id {} уже получено.", bookingId);
            throw new BookingBadRequest("Подтверждение уже получено.");
        }

        if (bookingRepository.existsByItem_IdAndStartBeforeAndEndAfter(item.getId(), booking.getStart(), booking.getEnd())) {
            log.debug("На данное время {} предмет c id {} уже забронирован", booking.getStart(), item.getId());
            throw new BookingNotFoundException("На данное время предмет забронирован");
        }

        if (item.getOwner().getId() != idUser) {
            log.debug("Пользователь с id {} пытается сделать подтверждение не своей вещи с id {}", idUser, item.getId());
            throw new BookingNotFoundException("Это не ваш предмет.");
        }

        if (approved) {
            booking.setStatus(BookingStatusEnum.APPROVED);
            log.info("Бронирование с id {} подтверждено.", bookingId);
            return toBookingDto(bookingRepository.save(booking));
        } else {
            booking.setStatus(BookingStatusEnum.REJECTED);
            log.info("Бронирование с id {} отклонено.", bookingId);
            return toBookingDto(bookingRepository.save(booking));
        }
    }

    @Override
    public List<BookingDto> getListAllReservationUser(int idUser, BookingStatusEnum state) {
        log.debug("Обрабатываем запрос на просмотр всех бронировании от владельца со статусом {}", state);

        LocalDateTime localDateTime = LocalDateTime.now();

        userRepository.findById(idUser)
                .orElseThrow(() -> {
                    log.debug("Пользователя с таким id {} не существует.", idUser);
                    return new UserNotFoundException(String.format("Пользователя с таким id %d не существует.", idUser));
                });

        switch (state) {
            case ALL:
                log.debug("Выводим список бронирований со статусом бронирования {}.", state);
                return mappingListBookingByTime(bookingRepository.findAllBookingByOwnerId(idUser));
            case PAST:
                log.debug("Выводим список бронирований со статусом бронирования {}.", state);
                return mappingListBookingByTime(bookingRepository.findByItem_Owner_IdAndEndIsBefore(idUser, localDateTime));
            case FUTURE:
                log.debug("Выводим список бронирований со статусом бронирования {}.", state);
                return mappingListBookingByTime(bookingRepository.findByItem_Owner_IdAndEndIsAfter(idUser, localDateTime));
            case CURRENT:
                log.debug("Выводим список бронирований со статусом бронирования {}.", state);
                return mappingListBookingByTime(bookingRepository.findByItem_Owner_IdAndStartBeforeAndEndAfter(idUser, localDateTime, localDateTime));
            case WAITING:
            case REJECTED:
                log.debug("Выводим список бронирований со статусом бронирования {}.", state);
                return mappingListBookingByTime(bookingRepository.findByItem_Owner_IdAndStatus(idUser, state));
        }

        log.debug("Не верный статус бронирования {}", state);
        throw new BookingBadRequest(String.format("Unknown state: %s", state));
    }
}
