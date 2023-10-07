package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.BookingStatusEnum;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByStatusAndBookerId(BookingStatusEnum status, int bookerId);

    List<Booking> findByBookerId(int bookerId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId")
    List<Booking> findAllBookingByOwnerId(int ownerId);

    List<Booking> findByItem_Owner_IdAndStatus(int ownerId, BookingStatusEnum status);

    List<Booking> findByEndIsBeforeAndBookerId(LocalDateTime localDateTime, int bookerId);

    List<Booking> findByItem_Owner_IdAndEndIsBefore(int ownerId, LocalDateTime localDateTime);

    List<Booking> findByStartIsAfterAndBookerId(LocalDateTime localDateTime, int bookerId);

    List<Booking> findByItem_Owner_IdAndEndIsAfter(int ownerId, LocalDateTime localDateTime);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfter(int ownerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByItem_Owner_IdAndStartBeforeAndEndAfter(int ownerId, LocalDateTime start, LocalDateTime end);

    boolean existsByItem_IdAndStartBeforeAndEndAfter(int itemId, LocalDateTime start, LocalDateTime end);

    @Query(value = "select b from Booking b where b.item.id = ?1 and b.item.owner.id = ?2 and b.status <> ?3 and b.start < ?4 order by b.start desc")
    List<Booking> findLastBookingByOwnerId(int itemId, int bookerId, BookingStatusEnum statusBooking, LocalDateTime currentTime);

    @Query(value = "select b from Booking b where b.item.id = ?1 and b.item.owner.id = ?2 and b.status <> ?3 and b.start > ?4 order by b.start")
    List<Booking> findNextBookingByOwnerId(int itemId, int bookerId, BookingStatusEnum statusBooking, LocalDateTime currentTime);

    boolean existsByBookerIdAndItem_IdAndStatusAndEndBefore(int bookerId, int itemId, BookingStatusEnum status, LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN :itemIds")
    List<Booking> findBookingsByItemIds(List<Integer> itemIds);
}