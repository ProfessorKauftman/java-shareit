package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE b.booker_id = ?1 " +
            "ORDER BY b.start_date DESC", nativeQuery = true)
    List<Booking> findAllBookingsByBookerId(Long userId);

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE b.booker_id = ?1 " +
            "AND ?2 BETWEEN b.start_date AND b.end_date " +
            "ORDER BY b.start_date DESC", nativeQuery = true)
    List<Booking> findAllCurrentBookingsByBookerId(Long userId, LocalDateTime currentTime);

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE b.booker_id = ?1 " +
            "AND b.end_date < ?2 " +
            "ORDER BY b.start_date DESC", nativeQuery = true)
    List<Booking> findAllPastBookingsByBookerId(Long userId, LocalDateTime currentTime);

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE b.booker_id = ?1 " +
            "AND b.start_date > ?2 " +
            "ORDER BY b.start_date DESC", nativeQuery = true)
    List<Booking> findAllFutureBookingsByBookerId(Long userId, LocalDateTime currentTime);

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE b.booker_id = ?1 " +
            "AND b.status = 'WAITING' " +
            "AND b.start_date > ?2 " +
            "ORDER BY b.start_date DESC", nativeQuery = true)
    List<Booking> findAllWaitingBookingsByBookerId(Long userId, LocalDateTime currentTime);

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE b.booker_id = ?1 " +
            "AND b.status = 'REJECTED' " +
            "ORDER BY b.start_date DESC", nativeQuery = true)
    List<Booking> findAllRejectedBookingsByBookerId(Long userId, LocalDateTime currentTime);

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE i.owner_id = ?1 " +
            "ORDER BY b.start_date DESC", nativeQuery = true)
    List<Booking> findAllBookingsByOwnerId(Long userId);

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE i.owner_id = ?1 " +
            "AND ?2 BETWEEN b.start_date AND b.end_date " +
            "ORDER BY b.start_date DESC", nativeQuery = true)
    List<Booking> findAllCurrentBookingsByOwnerId(Long userId, LocalDateTime currentTime);

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE i.owner_id = ?1 " +
            "AND b.end_date < ?2 " +
            "ORDER BY b.start_date DESC", nativeQuery = true)
    List<Booking> findAllPastBookingsByOwnerId(Long userId, LocalDateTime currentTime);

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE i.owner_id = ?1 " +
            "AND b.start_date > ?2 " +
            "ORDER BY b.start_date DESC", nativeQuery = true)
    List<Booking> findAllFutureBookingsByOwnerId(Long userId, LocalDateTime currentTime);

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE i.owner_id = ?1 " +
            "AND b.status = 'WAITING' " +
            "AND b.start_date > ?2 " +
            "ORDER BY b.start_date DESC", nativeQuery = true)
    List<Booking> findAllWaitingBookingsByOwnerId(Long userId, LocalDateTime currentTime);

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE i.owner_id = ?1 " +
            "AND b.status = 'REJECTED' " +
            "ORDER BY b.start_date DESC", nativeQuery = true)
    List<Booking> findAllRejectedBookingsByOwnerId(Long userId);

    @Query(value = "SELECT * FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE b.item_id = ?1 " +
            "AND b.start_date < ?2 " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.start_date DESC LIMIT 1", nativeQuery = true)
    Optional<Booking> getLastBooking(Long itemId, LocalDateTime currentTime);

    @Query(value = "SELECT * FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE b.item_id = ?1 " +
            "AND b.start_date > ?2 " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.start_date DESC LIMIT 1", nativeQuery = true)
    Optional<Booking> getNextBooking(Long itemId, LocalDateTime currentTime);

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE b.booker_id = ?1 " +
            "AND i.id = ?2 " +
            "AND b.status = 'APPROVED' " +
            "AND b.end_date < ?3 ", nativeQuery = true)
    List<Booking> findAllByUserBookings(Long userId, Long itemId, LocalDateTime now);

    List<Booking> findAllByItemInAndStatusOrderByStartAsc(List<Item> items, Status status);


    List<Booking> findAllByItemAndStatusOrderByStartAsc(Item item, Status bookingStatus);

}
