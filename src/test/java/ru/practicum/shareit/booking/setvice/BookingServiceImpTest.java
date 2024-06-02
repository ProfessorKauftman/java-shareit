package ru.practicum.shareit.booking.setvice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImpTest {

    @Mock
    private UserService userService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImp bookingServiceImp;


    private final User user = User.builder()
            .id(1L)
            .name("Professors")
            .email("professor@yandex.ru")
            .build();

    private final User owner = User.builder()
            .id(2L)
            .name("ProfessorsOwner")
            .email("professorOwner@yandex.ru")
            .build();

    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("Professors")
            .email("professor@yandex.ru")
            .build();

    private final Item item = Item.builder()
            .id(1L)
            .name("Lopata")
            .description("Lopata description")
            .available(true)
            .owner(owner)
            .build();

    private final Booking booking = Booking.builder()
            .id(1L)
            .item(item)
            .booker(user)
            .status(Status.APPROVED)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().plusDays(2L))
            .build();

    private final Booking bookingWaiting = Booking.builder()
            .id(1L)
            .item(item)
            .booker(user)
            .status(Status.WAITING)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().plusDays(2L))
            .build();

    private final BookingDto bookingDto = BookingDto.builder()
            .itemId(1L)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().plusDays(2L))
            .build();

    private final BookingDto bookingDtoEndBeforeStart = BookingDto.builder()
            .itemId(1L)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().minusDays(1L))
            .build();

    @Test
    void whenCreateIsOk() {
        BookingDtoOut bookingDtoOut = BookingMapper.toBookingOut(BookingMapper.toBooking(user, item, bookingDto));

        when(userService.findById(userDto.getId())).thenReturn(userDto);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(BookingMapper.toBooking(user, item, bookingDto));

        BookingDtoOut realBookingDtoOut = bookingServiceImp.add(userDto.getId(), bookingDto);

        assertEquals(bookingDtoOut, realBookingDtoOut);
    }

    @Test
    void whenCreateEndIsBeforeStartShouldThrowValidationException() {
        when(userService.findById(userDto.getId())).thenReturn(userDto);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ValidationException validationException = assertThrows(ValidationException.class,
                () -> bookingServiceImp.add(userDto.getId(), bookingDtoEndBeforeStart));

        assertEquals(validationException.getMessage(),
                "The end date cannot be earlier than or equal to the start date");

    }

    @Test
    void whenCreateItemIsNotAvailableShouldThrowValidationException() {
        item.setAvailable(false);

        when(userService.findById(userDto.getId())).thenReturn(userDto);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ValidationException validationException = assertThrows(ValidationException.class,
                () -> bookingServiceImp.add(userDto.getId(), bookingDto));

        assertEquals(validationException.getMessage(), "The item is not available for booking");
    }

    @Test
    void whenCreateItemOwnerEqualsBookerShouldThrowNotFoudException() {
        item.setOwner(user);

        when(userService.findById(userDto.getId())).thenReturn(userDto);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingServiceImp.add(userDto.getId(), bookingDto));

        assertEquals(notFoundException.getMessage(), "The item was not found");
    }

    @Test
    void whenUpdateIsOk() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingWaiting));
        when(bookingRepository.save(any(Booking.class))).thenReturn(bookingWaiting);

        BookingDtoOut bookingDtoOut = bookingServiceImp.update(owner.getId(), bookingWaiting.getId(), true);

        assertEquals(Status.APPROVED, bookingDtoOut.getStatus());
    }

    @Test
    void whenUpdateAndStatusNotApproved() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingWaiting));
        when(bookingRepository.save(any(Booking.class))).thenReturn(bookingWaiting);

        BookingDtoOut bookingDtoOut = bookingServiceImp.update(owner.getId(), bookingWaiting.getId(), false);

        assertEquals(Status.REJECTED, bookingDtoOut.getStatus());
    }

    @Test
    void whenUpdateShouldBeNotWaiting() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        ValidationException validationException = assertThrows(ValidationException.class,
                () -> bookingServiceImp.update(owner.getId(), booking.getId(), false));

        assertEquals(validationException.getMessage(), "Booking with not the WAITING status");
    }

    @Test
    void whenUpdateAndUserIsNotItemOwnerShouldThrowNotFoundException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingServiceImp.update(userDto.getId(), booking.getId(), true));

        assertEquals(notFoundException.getMessage(), "The user is not the owner");
    }

    @Test
    void whenGetByIdIsOk() {
        BookingDtoOut bookingDtoOut = BookingMapper.toBookingOut(booking);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDtoOut realBookingDtoOut = bookingServiceImp.findBookingByUserId(user.getId(), booking.getId());

        assertEquals(bookingDtoOut, realBookingDtoOut);
    }

    @Test
    void whenGetBookingByIdIsNotValidShouldThrowNotFoundException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingServiceImp.findBookingByUserId(1L, booking.getId()));

        assertEquals(notFoundException.getMessage(), "The booking was not found");
    }

    @Test
    void whenGetByIdAndUserIsNotItemOwnerShouldThrowNotFoundException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingServiceImp.findBookingByUserId(3L, booking.getId()));

        assertEquals(notFoundException.getMessage(), "The user is not the owner or the author of the reservation");
    }

    @Test
    void whenGetAllByBookerAndBookingStateALL() {
        List<BookingDtoOut> bookingDtoOuts = List.of(BookingMapper.toBookingOut(booking));

        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllBookingsByBookerId(anyLong(), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDtoOut> realBookingDtoOut = bookingServiceImp
                .findAllForBooker(user.getId(), "ALL", 0, 10);
        assertEquals(bookingDtoOuts, realBookingDtoOut);
    }

    @Test
    void whenGetAllByBookerAndBookingStateCURRENT() {
        List<BookingDtoOut> bookingDtoOuts = List.of(BookingMapper.toBookingOut(booking));

        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllCurrentBookingsByBookerId(anyLong(),
                any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDtoOut> realBookingDtoOut = bookingServiceImp
                .findAllForBooker(user.getId(), "CURRENT", 0, 10);
        assertEquals(bookingDtoOuts, realBookingDtoOut);
    }

    @Test
    void whenGetAllByBookerAndBookingStatePAST() {
        List<BookingDtoOut> bookingDtoOuts = List.of(BookingMapper.toBookingOut(booking));

        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllPastBookingsByBookerId(anyLong(),
                any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDtoOut> realBookingDtoOut = bookingServiceImp
                .findAllForBooker(user.getId(), "PAST", 0, 10);
        assertEquals(bookingDtoOuts, realBookingDtoOut);
    }

    @Test
    void whenGetAllByBookerAndBookingStateFUTURE() {
        List<BookingDtoOut> bookingDtoOuts = List.of(BookingMapper.toBookingOut(booking));

        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllFutureBookingsByBookerId(anyLong(),
                any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDtoOut> realBookingDtoOut = bookingServiceImp
                .findAllForBooker(user.getId(), "FUTURE", 0, 10);
        assertEquals(bookingDtoOuts, realBookingDtoOut);
    }

    @Test
    void whenGetAllByBookerAndBookingStateWAITING() {
        List<BookingDtoOut> bookingDtoOuts = List.of(BookingMapper.toBookingOut(booking));

        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllWaitingBookingsByBookerId(anyLong(),
                any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDtoOut> realBookingDtoOut = bookingServiceImp
                .findAllForBooker(user.getId(), "WAITING", 0, 10);
        assertEquals(bookingDtoOuts, realBookingDtoOut);
    }

    @Test
    void whenGetAllByBookerAndBookingStateREJECTED() {
        List<BookingDtoOut> bookingDtoOuts = List.of(BookingMapper.toBookingOut(booking));

        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllRejectedBookingsByBookerId(anyLong(),
                any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDtoOut> realBookingDtoOut = bookingServiceImp
                .findAllForBooker(user.getId(), "REJECTED", 0, 10);
        assertEquals(bookingDtoOuts, realBookingDtoOut);
    }

    @Test
    void whenGetAllByBookerAndBookingStateIsNotValidShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> bookingServiceImp.findAllForBooker(user.getId(), "ERROR", 0, 10));
    }

    @Test
    void whenGetAllByOwnerAndBookingStateALL() {
        List<BookingDtoOut> bookingDtoOuts = List.of(BookingMapper.toBookingOut(booking));

        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllBookingsByOwnerId(anyLong(), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDtoOut> realBookingDtoOut = bookingServiceImp
                .findAllForOwner(user.getId(), "ALL", 0, 10);
        assertEquals(bookingDtoOuts, realBookingDtoOut);
    }

    @Test
    void whenGetAllByOwnerAndBookingStateCURRENT() {
        List<BookingDtoOut> bookingDtoOuts = List.of(BookingMapper.toBookingOut(booking));

        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllCurrentBookingsByOwnerId(anyLong(),
                any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDtoOut> realBookingDtoOut = bookingServiceImp
                .findAllForOwner(user.getId(), "CURRENT", 0, 10);
        assertEquals(bookingDtoOuts, realBookingDtoOut);
    }

    @Test
    void whenGetAllByOwnerAndBookingStatePAST() {
        List<BookingDtoOut> bookingDtoOuts = List.of(BookingMapper.toBookingOut(booking));

        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllPastBookingsByOwnerId(anyLong(),
                any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDtoOut> realBookingDtoOut = bookingServiceImp
                .findAllForOwner(user.getId(), "PAST", 0, 10);
        assertEquals(bookingDtoOuts, realBookingDtoOut);
    }

    @Test
    void whenGetAllByOwnerAndBookingStateFUTURE() {
        List<BookingDtoOut> bookingDtoOuts = List.of(BookingMapper.toBookingOut(booking));

        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllFutureBookingsByOwnerId(anyLong(),
                any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDtoOut> realBookingDtoOut = bookingServiceImp
                .findAllForOwner(user.getId(), "FUTURE", 0, 10);
        assertEquals(bookingDtoOuts, realBookingDtoOut);
    }

    @Test
    void whenGetAllByOwnerAndBookingStateWAITING() {
        List<BookingDtoOut> bookingDtoOuts = List.of(BookingMapper.toBookingOut(booking));

        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllWaitingBookingsByOwnerId(anyLong(),
                any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDtoOut> realBookingDtoOut = bookingServiceImp
                .findAllForOwner(user.getId(), "WAITING", 0, 10);
        assertEquals(bookingDtoOuts, realBookingDtoOut);
    }

    @Test
    void whenGetAllByOwnerAndBookingStateREJECTED() {
        List<BookingDtoOut> bookingDtoOuts = List.of(BookingMapper.toBookingOut(booking));

        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllRejectedBookingsByOwnerId(anyLong(),
                any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDtoOut> realBookingDtoOut = bookingServiceImp
                .findAllForOwner(user.getId(), "REJECTED", 0, 10);
        assertEquals(bookingDtoOuts, realBookingDtoOut);
    }

    @Test
    void whenGetAllByOwnerAndBookingStateIsNotValidShouldThrowIllegalArgumentException() {
        when(userService.findById(user.getId())).thenReturn(userDto);

        assertThrows(IllegalArgumentException.class,
                () -> bookingServiceImp.findAllForBooker(user.getId(), "ERROR", 0, 10));
    }


}