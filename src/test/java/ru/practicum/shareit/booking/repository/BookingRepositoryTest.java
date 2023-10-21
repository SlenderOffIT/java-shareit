package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.BookingStatusEnum;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    Sort sort;
    Pageable pageable;
    User user;
    User user2;
    User user3;
    Item item;
    Item item2;

    @BeforeEach
    public void before() {
        sort = Sort.by(Sort.Order.desc("start"));
        pageable = PageRequest.of(0 / 10, 10, sort);
        user = new User("Вася", "asdfgh@gmail.com");
        user2 = new User("Петя", "dfgh@gmail.com");
        user3 = new User("Ваня", "gggeeef@mail.ru");
        item = new Item("item", "description", false, user);
        item2 = new Item("item1", "description1", true, user);
    }

    @Test
    public void statusWaitingRejectedBooker() {
        User create = userRepository.save(user);
        Item itemCreated = itemRepository.save(item);

        Booking booking = new Booking(LocalDateTime.now(), LocalDateTime.now().plusHours(2));
        booking.setBooker(create);
        booking.setItem(itemCreated);
        booking.setStatus(BookingStatusEnum.WAITING);
        bookingRepository.save(booking);

        List<Booking> bookingPage = bookingRepository.findByStatusAndBookerId(BookingStatusEnum.WAITING, create.getId(), pageable);

        assertEquals(1, bookingPage.size());
        assertEquals(booking, bookingPage.get(0));

        List<Booking> bookingList2 = bookingRepository.findByStatusAndBookerId(BookingStatusEnum.REJECTED, create.getId(), pageable);

        assertEquals(0, bookingList2.size());
    }

    @Test
    public void statusAllBooker() {
        User create = userRepository.save(user);
        Item itemCreated = itemRepository.save(item);

        Booking booking = new Booking(LocalDateTime.now(), LocalDateTime.now().plusHours(2));
        booking.setBooker(create);
        booking.setItem(itemCreated);
        booking.setStatus(BookingStatusEnum.WAITING);
        bookingRepository.save(booking);

        List<Booking> bookingList = bookingRepository.findByBookerId(create.getId(), pageable);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    public void statusAllOwner() {
        User create = userRepository.save(user);
        User create2 = userRepository.save(user2);
        Item itemCreated = itemRepository.save(item);

        Booking booking = new Booking(LocalDateTime.now(), LocalDateTime.now().plusHours(2));
        booking.setBooker(create2);
        booking.setItem(itemCreated);
        booking.setStatus(BookingStatusEnum.WAITING);
        bookingRepository.save(booking);

        List<Booking> bookingList = bookingRepository.findAllBookingByOwnerId(create.getId(), pageable);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    public void statusWaitingRejectedOwner() {
        User create = userRepository.save(user);
        Item itemCreated = itemRepository.save(item);

        Booking booking = new Booking(LocalDateTime.now(), LocalDateTime.now().plusHours(2));
        booking.setBooker(create);
        booking.setItem(itemCreated);
        booking.setStatus(BookingStatusEnum.WAITING);
        bookingRepository.save(booking);

        List<Booking> bookingList = bookingRepository.findByItem_Owner_IdAndStatus(itemCreated.getOwner().getId(),
                BookingStatusEnum.WAITING, pageable);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));

        booking.setStatus(BookingStatusEnum.REJECTED);
        bookingRepository.save(booking);

        bookingList = bookingRepository.findByItem_Owner_IdAndStatus(itemCreated.getOwner().getId(),
                BookingStatusEnum.REJECTED, pageable);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    public void statusPastBooker() {
        User create = userRepository.save(user);
        Item itemCreated = itemRepository.save(item);

        Booking booking = new Booking(LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1));
        booking.setBooker(create);
        booking.setItem(itemCreated);
        booking.setStatus(BookingStatusEnum.PAST);
        bookingRepository.save(booking);

        List<Booking> bookingList = bookingRepository.findByEndIsBeforeAndBookerId(LocalDateTime.now(), create.getId(), pageable);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    public void statusPastOwner() {
        User create = userRepository.save(user);
        Item itemCreated = itemRepository.save(item);

        Booking booking = new Booking(LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1));
        booking.setBooker(create);
        booking.setItem(itemCreated);
        booking.setStatus(BookingStatusEnum.PAST);
        bookingRepository.save(booking);

        List<Booking> bookingList = bookingRepository.findByItem_Owner_IdAndEndIsBefore(itemCreated.getOwner().getId(),
                LocalDateTime.now(),pageable);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    public void statusFutureBooker() {
        User create = userRepository.save(user);
        Item itemCreated = itemRepository.save(item);

        Booking booking = new Booking(LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));
        booking.setBooker(create);
        booking.setItem(itemCreated);
        booking.setStatus(BookingStatusEnum.FUTURE);
        bookingRepository.save(booking);

        List<Booking> bookingList = bookingRepository.findByStartIsAfterAndBookerId(LocalDateTime.now(), create.getId(), pageable);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    public void statusFutureOwner() {
        User create = userRepository.save(user);
        Item itemCreated = itemRepository.save(item);

        Booking booking = new Booking(LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));
        booking.setBooker(create);
        booking.setItem(itemCreated);
        booking.setStatus(BookingStatusEnum.FUTURE);
        bookingRepository.save(booking);

        List<Booking> bookingList = bookingRepository.findByItem_Owner_IdAndEndIsAfter(itemCreated.getOwner().getId(),
                LocalDateTime.now(),pageable);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    public void statusCurrentBooker() {
        User create = userRepository.save(user);
        Item itemCreated = itemRepository.save(item);

        Booking booking = new Booking(LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(2));
        booking.setBooker(create);
        booking.setItem(itemCreated);
        booking.setStatus(BookingStatusEnum.CURRENT);
        bookingRepository.save(booking);

        List<Booking> bookingList = bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(create.getId(),
                LocalDateTime.now(), LocalDateTime.now(), pageable);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    public void statusCurrentOwner() {
        User create = userRepository.save(user);
        Item itemCreated = itemRepository.save(item);

        Booking booking = new Booking(LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(2));
        booking.setBooker(create);
        booking.setItem(itemCreated);
        booking.setStatus(BookingStatusEnum.CURRENT);
        bookingRepository.save(booking);

        List<Booking> bookingList = bookingRepository.findByItem_Owner_IdAndStartBeforeAndEndAfter(
                itemCreated.getOwner().getId(), LocalDateTime.now(), LocalDateTime.now(), pageable);

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    public void booleanTimeBooking() {
        User create = userRepository.save(user);
        Item itemCreated = itemRepository.save(item);

        Booking booking = new Booking(LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(2));
        booking.setBooker(create);
        booking.setItem(itemCreated);
        booking.setStatus(BookingStatusEnum.APPROVED);
        bookingRepository.save(booking);

        boolean isAvailable = bookingRepository.existsByItem_IdAndStartBeforeAndEndAfter(itemCreated.getId(),
                LocalDateTime.now(), LocalDateTime.now().plusHours(1));

        assertTrue(isAvailable);
    }

    @Test
    public void lastBooking() {
        User create = userRepository.save(user);
        User create2 = userRepository.save(user);
        Item itemCreated = itemRepository.save(item);

        Booking booking = new Booking(LocalDateTime.now().minusHours(1), LocalDateTime.now().minusMinutes(30));
        booking.setBooker(create2);
        booking.setItem(itemCreated);
        booking.setStatus(BookingStatusEnum.APPROVED);
        bookingRepository.save(booking);

        Booking booking1 = new Booking(LocalDateTime.now().minusHours(3), LocalDateTime.now().minusHours(2));
        booking1.setBooker(create);
        booking1.setItem(itemCreated);
        booking1.setStatus(BookingStatusEnum.APPROVED);
        bookingRepository.save(booking1);

        List<Booking> bookingList = bookingRepository.findLastBookingByOwnerId(
                itemCreated.getId(), create.getId(), BookingStatusEnum.REJECTED, LocalDateTime.now());

        assertEquals(booking, bookingList.get(0));
    }

    @Test
    public void nextBooking() {
        User create = userRepository.save(user);
        User create2 = userRepository.save(user);
        Item itemCreated = itemRepository.save(item);

        Booking booking = new Booking(LocalDateTime.now().plusHours(10), LocalDateTime.now().plusHours(12));
        booking.setBooker(create2);
        booking.setItem(itemCreated);
        booking.setStatus(BookingStatusEnum.APPROVED);
        bookingRepository.save(booking);

        Booking booking1 = new Booking(LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));
        booking1.setBooker(create);
        booking1.setItem(itemCreated);
        booking1.setStatus(BookingStatusEnum.APPROVED);
        bookingRepository.save(booking1);

        List<Booking> bookingList = bookingRepository.findNextBookingByOwnerId(
                itemCreated.getId(), create.getId(), BookingStatusEnum.REJECTED, LocalDateTime.now());

        assertEquals(booking1, bookingList.get(0));
    }

    @Test
    public void booleanCommentBooking() {
        User create2 = userRepository.save(user);
        User create3 = userRepository.save(user3);
        Item itemCreated = itemRepository.save(item);

        Booking booking = new Booking(LocalDateTime.now().minusHours(5), LocalDateTime.now().minusHours(3));
        booking.setBooker(create2);
        booking.setItem(itemCreated);
        booking.setStatus(BookingStatusEnum.APPROVED);
        bookingRepository.save(booking);

        boolean isAvailable = bookingRepository.existsByBookerIdAndItem_IdAndStatusAndEndBefore(
                create2.getId(), itemCreated.getId(), BookingStatusEnum.APPROVED, LocalDateTime.now());

        assertTrue(isAvailable);

        isAvailable = bookingRepository.existsByBookerIdAndItem_IdAndStatusAndEndBefore(
                create3.getId(), itemCreated.getId(), BookingStatusEnum.APPROVED, LocalDateTime.now());

        assertFalse(isAvailable);
    }

    @Test
    public void allBookingAllItems() {
        User create2 = userRepository.save(user);
        User create3 = userRepository.save(user3);
        Item itemCreated = itemRepository.save(item);
        Item item3 = new Item("item1", "description1", true, user);
        Item itemCreated3 = itemRepository.save(item3);

        Booking booking = new Booking(LocalDateTime.now().minusHours(5), LocalDateTime.now().minusHours(3));
        booking.setBooker(create2);
        booking.setItem(itemCreated);
        booking.setStatus(BookingStatusEnum.APPROVED);
        bookingRepository.save(booking);

        Booking booking2 = new Booking(LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1));
        booking2.setBooker(create3);
        booking2.setItem(itemCreated3);
        booking2.setStatus(BookingStatusEnum.APPROVED);
        bookingRepository.save(booking2);

        List<Integer> itemsId = Arrays.asList(itemCreated.getId(), itemCreated3.getId());

        List<Booking> bookingList = bookingRepository.findBookingsByItemIds(itemsId);

        assertEquals(2, bookingList.size());
        assertEquals(booking, bookingList.get(0));
        assertEquals(booking2, bookingList.get(1));
    }
}