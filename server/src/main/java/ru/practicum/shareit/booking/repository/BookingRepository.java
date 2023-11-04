package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.BookingStatusEnum;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByStatusAndBookerId(BookingStatusEnum status, int bookerId, Pageable pageable);

    List<Booking> findByBookerId(int bookerId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId")
    List<Booking> findAllBookingByOwnerId(int ownerId, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndStatus(int ownerId, BookingStatusEnum status, Pageable pageable);

    List<Booking> findByEndIsBeforeAndBookerId(LocalDateTime localDateTime, int bookerId, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndEndIsBefore(int ownerId, LocalDateTime localDateTime, Pageable pageable);

    List<Booking> findByStartIsAfterAndBookerId(LocalDateTime localDateTime, int bookerId, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndEndIsAfter(int ownerId, LocalDateTime localDateTime, Pageable pageable);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfter(int ownerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndStartBeforeAndEndAfter(int ownerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    boolean existsByItem_IdAndStartBeforeAndEndAfter(int itemId, LocalDateTime start, LocalDateTime end);

    @Query(value = "select b from Booking b where b.item.id = ?1 and b.item.owner.id = ?2 and b.status <> ?3 and b.start < ?4 order by b.start desc")
    List<Booking> findLastBookingByOwnerId(int itemId, int bookerId, BookingStatusEnum statusBooking, LocalDateTime currentTime);

    @Query(value = "select b from Booking b where b.item.id = ?1 and b.item.owner.id = ?2 and b.status <> ?3 and b.start > ?4 order by b.start")
    List<Booking> findNextBookingByOwnerId(int itemId, int bookerId, BookingStatusEnum statusBooking, LocalDateTime currentTime);

    boolean existsByBookerIdAndItem_IdAndStatusAndEndBefore(int bookerId, int itemId, BookingStatusEnum status, LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN :itemIds")
    List<Booking> findBookingsByItemIds(List<Integer> itemIds);
}