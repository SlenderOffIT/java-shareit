package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.booking.dto.MapperBookingDto.mappingListBookingByTime;
import static ru.practicum.shareit.booking.dto.MapperBookingDto.toBooking;
import static ru.practicum.shareit.booking.dto.MapperBookingDto.toBookingDto;
import static ru.practicum.shareit.util.Constant.LOG_LIST_STATUS;
import static ru.practicum.shareit.util.Constant.NOT_FOUND_BOOKING;
import static ru.practicum.shareit.util.Constant.NOT_FOUND_USER;
import static ru.practicum.shareit.util.Validation.validate;

@Service
@Slf4j
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private BookingRepository bookingRepository;
    private ItemRepository itemRepository;
    private UserRepository userRepository;

    @Override
    @Transactional
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

        User user = exceptionIfNotUser(idUser);

        book.setBooker(user);
        book.setStatus(BookingStatusEnum.WAITING);
        return toBookingDto(bookingRepository.save(book));
    }

    @Override
    @Transactional
    public BookingDto getBookingById(int idUser, int bookingId) {
        log.debug("Обрабатываем запрос на просмотр бронирования с id {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.warn(NOT_FOUND_BOOKING.getValue(), bookingId);
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
    @Transactional
    public List<BookingDto> getAllBookingsUser(BookingStatusEnum state, int idUser, int from, int size) {
        log.debug("Обрабатываем запрос на просмотр всех бронирований от арендатора со статусом {}", state);

        if (from < 0 || size < 1) {
            log.warn("Отрицательный параметр страницы.");
            throw new ValidationException("Параметры страниц не могут быть отрицательными");
        }

        Sort sort = Sort.by(Sort.Order.desc("start"));
        Pageable pageable = PageRequest.of(from / size, size, sort);
        LocalDateTime localDateTime = LocalDateTime.now();

        exceptionIfNotUser(idUser);

        switch (state) {
            case ALL:
                log.debug(LOG_LIST_STATUS.getValue(), state);
                List<Booking> bookings = bookingRepository.findByBookerId(idUser, pageable);
                return mappingListBookingByTime(bookings);
            case PAST:
                log.debug(LOG_LIST_STATUS.getValue(), state);
                List<Booking> bookingsPast = bookingRepository.findByEndIsBeforeAndBookerId(localDateTime, idUser, pageable);
                return mappingListBookingByTime(bookingsPast);
            case FUTURE:
                log.debug(LOG_LIST_STATUS.getValue(), state);
                List<Booking> bookingsFuture = bookingRepository.findByStartIsAfterAndBookerId(localDateTime, idUser, pageable);
                return mappingListBookingByTime(bookingsFuture);
            case CURRENT:
                log.debug(LOG_LIST_STATUS.getValue(), state);
                List<Booking> bookingsCurrent = bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(idUser, localDateTime, localDateTime, pageable);
                return mappingListBookingByTime(bookingsCurrent);
            case WAITING:
            case REJECTED:
                log.debug(LOG_LIST_STATUS.getValue(), state);
                List<Booking> bookingsWaitingAndRejected = bookingRepository.findByStatusAndBookerId(state, idUser, pageable);
                return mappingListBookingByTime(bookingsWaitingAndRejected);
        }
        log.warn("Не верный статус бронирования {}", state);
        throw new BookingBadRequest(String.format("Unknown state: %s", state));
    }

    @Override
    @Transactional
    public BookingDto patchApproved(Boolean approved, Integer bookingId, Integer idUser) {
        log.debug("Обрабатываем запрос на одобрение(отклонение) бронирования с id {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.warn(NOT_FOUND_BOOKING.getValue(), bookingId);
                    return new BookingNotFoundException(String.format("Бронирования с id %d не существует.", bookingId));
                });

        Item item = booking.getItem();
        User user = item.getOwner();

        if (booking.getStatus().equals(BookingStatusEnum.APPROVED)) {
            log.debug("Подтверждение на бронирование с id {} уже получено.", bookingId);
            throw new BookingBadRequest("Подтверждение уже получено.");
        }

        if (bookingRepository.existsByItem_IdAndStartBeforeAndEndAfter(item.getId(), booking.getStart(), booking.getEnd())) {
            log.debug("На данное время {} предмет c id {} уже забронирован", booking.getStart(), item.getId());
            throw new BookingNotFoundException("На данное время предмет забронирован");
        }

        if (user.getId() != idUser) {
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
    @Transactional
    public List<BookingDto> getListAllReservationUser(int idUser, BookingStatusEnum state, int from, int size) {
        log.debug("Обрабатываем запрос на просмотр всех бронировании от владельца со статусом {}", state);

        if (from < 0 || size < 1) {
            log.warn("Отрицательный параметр страницы.");
            throw new ValidationException("Параметры страниц не могут быть отрицательными");
        }

        Sort sort = Sort.by(Sort.Order.desc("start"));
        Pageable pageable = PageRequest.of(from / size, size, sort);
        LocalDateTime localDateTime = LocalDateTime.now();

        exceptionIfNotUser(idUser);

        switch (state) {
            case ALL:
                log.debug(LOG_LIST_STATUS.getValue(), state);
                return mappingListBookingByTime(bookingRepository.findAllBookingByOwnerId(idUser, pageable));
            case PAST:
                log.debug(LOG_LIST_STATUS.getValue(), state);
                return mappingListBookingByTime(bookingRepository.findByItem_Owner_IdAndEndIsBefore(idUser, localDateTime, pageable));
            case FUTURE:
                log.debug(LOG_LIST_STATUS.getValue(), state);
                return mappingListBookingByTime(bookingRepository.findByItem_Owner_IdAndEndIsAfter(idUser, localDateTime, pageable));
            case CURRENT:
                log.debug(LOG_LIST_STATUS.getValue(), state);
                return mappingListBookingByTime(bookingRepository.findByItem_Owner_IdAndStartBeforeAndEndAfter(idUser, localDateTime, localDateTime, pageable));
            case WAITING:
            case REJECTED:
                log.debug(LOG_LIST_STATUS.getValue(), state);
                return mappingListBookingByTime(bookingRepository.findByItem_Owner_IdAndStatus(idUser, state, pageable));
        }

        log.warn("Не верный статус бронирования {}", state);
        throw new BookingBadRequest(String.format("Unknown state: %s", state));
    }

    private User exceptionIfNotUser(int idUser) {
        return userRepository.findById(idUser)
                .orElseThrow(() -> {
                    log.warn(NOT_FOUND_USER.getValue(), idUser);
                    return new UserNotFoundException(String.format("Пользователя с таким id %d не существует.", idUser));
                });
    }
}
