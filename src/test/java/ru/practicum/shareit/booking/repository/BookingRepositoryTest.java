package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingRepositoryTest {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    TestEntityManager testEntityManager;

    private Long userId;
    private Long itemId;

    @BeforeEach
    public void init() {
        User user = User.builder()
                .name("Professors")
                .email("professor@yandex.ru")
                .build();
        user = testEntityManager.persistFlushFind(user);
        userId = user.getId();

        User owner = User.builder()
                .name("ProfessorsOwner")
                .email("professorOwner@yandex.ru")
                .build();
        owner = testEntityManager.persistFlushFind(owner);

        Item item = Item.builder()
                .name("Lopata")
                .description("Lopata description")
                .available(true)
                .owner(owner)
                .build();
        item = testEntityManager.persistFlushFind(item);
        itemId = item.getId();

        final Booking booking = Booking.builder()
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .start(LocalDateTime.now().minusHours(1L))
                .end(LocalDateTime.now().plusWeeks(1L))
                .build();
        testEntityManager.persistAndFlush(booking);

        final Booking pastBooking = Booking.builder()
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .start(LocalDateTime.now().minusDays(2L))
                .end(LocalDateTime.now().minusWeeks(1L))
                .build();
        testEntityManager.persistAndFlush(pastBooking);

        final Booking futureBooking = Booking.builder()
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .start(LocalDateTime.now().plusDays(1L))
                .end(LocalDateTime.now().plusWeeks(2L))
                .build();
        testEntityManager.persistAndFlush(futureBooking);
    }

    @AfterEach
    public void deleteAll() {
        bookingRepository.deleteAll();
    }

    @Test
    void whenFindAllByBookerIdIsOk() {
        List<Booking> bookings = bookingRepository.findAllBookingsByBookerId(1L, PageRequest.of(0, 10));
        assertEquals(bookings.size(), 3);
        assertEquals(bookings.get(0).getBooker().getId(), 1L);
    }

    @Test
    void whenFindAllCurrentBookingsByBookerIdIsOk() {
        List<Booking> bookings = bookingRepository.findAllCurrentBookingsByBookerId(1L, LocalDateTime.now(),
                PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getBooker().getId(), 1L);
    }

    @Test
    void whenFindAllPastBookingsByBookerIdIsOk() {
        List<Booking> bookings = bookingRepository.findAllPastBookingsByBookerId(1L, LocalDateTime.now(),
                PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getId(), 2L);
    }

    @Test
    void whenFindAllFutureBookingsByBookerIdIsOk() {
        List<Booking> bookings = bookingRepository.findAllFutureBookingsByBookerId(1L, LocalDateTime.now(),
                PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getId(), 3L);
    }

    @Test
    void whenFindAllWaitingBookingsByBookerIdIsOk() {
        User user = testEntityManager.find(User.class, userId);
        Item item = testEntityManager.find(Item.class, itemId);

        Booking waitingBooking = Booking.builder()
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .start(LocalDateTime.now().plusDays(1L))
                .end(LocalDateTime.now().plusDays(2L))
                .build();

        bookingRepository.save(waitingBooking);

        List<Booking> bookings = bookingRepository.findAllWaitingBookingsByBookerId(1L, LocalDateTime.now(),
                PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getStatus(), Status.WAITING);
    }

    @Test
    void whenFindAllRejectedBookingByBookerIdIsOk() {
        User user = testEntityManager.find(User.class, userId);
        Item item = testEntityManager.find(Item.class, itemId);
        Booking waitingBooking = Booking.builder()
                .item(item)
                .booker(user)
                .status(Status.REJECTED)
                .start(LocalDateTime.now().plusDays(1L))
                .end(LocalDateTime.now().plusDays(2L))
                .build();

        bookingRepository.save(waitingBooking);

        List<Booking> bookings = bookingRepository.findAllRejectedBookingsByBookerId(1L, LocalDateTime.now(),
                PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getStatus(), Status.REJECTED);
    }

    @Test
    void whenFindAllByOwnerIdIsOk() {
        List<Booking> bookings = bookingRepository.findAllBookingsByOwnerId(2L, PageRequest.of(0, 10));
        assertEquals(bookings.size(), 3);
        assertEquals(bookings.get(0).getBooker().getId(), 1L);
    }

    @Test
    void whenFindAllCurrentBookingsByOwnerIdIsOk() {
        List<Booking> bookings = bookingRepository.findAllCurrentBookingsByOwnerId(2L, LocalDateTime.now(),
                PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getBooker().getId(), 1L);
    }

    @Test
    void whenFindAllPastBookingsByOwnerIdIsOk() {
        List<Booking> bookings = bookingRepository.findAllPastBookingsByOwnerId(2L, LocalDateTime.now(),
                PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getId(), 2L);
    }

    @Test
    void whenFindAllFutureBookingsByOwnerIdIsOk() {
        List<Booking> bookings = bookingRepository.findAllFutureBookingsByOwnerId(2L, LocalDateTime.now(),
                PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getId(), 3L);
    }

    @Test
    void whenFindAllWaitingBookingsByOwnerIdIsOk() {
        User user = testEntityManager.find(User.class, userId);
        Item item = testEntityManager.find(Item.class, itemId);
        Booking waitingBooking = Booking.builder()
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .start(LocalDateTime.now().plusDays(1L))
                .end(LocalDateTime.now().plusDays(2L))
                .build();

        bookingRepository.save(waitingBooking);

        List<Booking> bookings = bookingRepository.findAllWaitingBookingsByOwnerId(2L, LocalDateTime.now(),
                PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getStatus(), Status.WAITING);
    }

    @Test
    void whenFindAllRejectedBookingByOwnerIdIsOk() {
        User user = testEntityManager.find(User.class, userId);
        Item item = testEntityManager.find(Item.class, itemId);
        Booking waitingBooking = Booking.builder()
                .item(item)
                .booker(user)
                .status(Status.REJECTED)
                .start(LocalDateTime.now().plusDays(1L))
                .end(LocalDateTime.now().plusDays(2L))
                .build();

        bookingRepository.save(waitingBooking);

        List<Booking> bookings = bookingRepository.findAllRejectedBookingsByOwnerId(2L,
                PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getStatus(), Status.REJECTED);
    }

    @Test
    void whenFindAllByUserBookingsIsOk() {
        List<Booking> bookings = bookingRepository.findAllByUserBookings(1L, 1L, LocalDateTime.now());

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getStatus(), Status.APPROVED);
    }

    @Test
    void whenGetLastBookingIsOk() {
        Optional<Booking> bookingOptional = bookingRepository.getLastBooking(1L, LocalDateTime.now());
        Booking realBooking;

        if (bookingOptional.isPresent()) {
            realBooking = bookingOptional.get();

            assertEquals(realBooking.getId(), 1L);
        } else {

            fail();
        }
    }

    @Test
    void whenGetNextBookingIsOk() {
        Optional<Booking> bookingOptional = bookingRepository.getNextBooking(1L, LocalDateTime.now());

        assertTrue(bookingOptional.isPresent(), "Booking should be present");

        Booking realBooking = bookingOptional.get();
        assertEquals(realBooking.getId(), 3L);
    }


}