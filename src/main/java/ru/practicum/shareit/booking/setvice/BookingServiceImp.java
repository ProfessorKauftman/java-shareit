package ru.practicum.shareit.booking.setvice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImp implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDtoOut add(Long userId, BookingDto bookingDto) {
        User user = UserMapper.toUser(userService.findById(userId));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("The item was not found"));
        bookingValidation(bookingDto, user, item);
        Booking booking = BookingMapper.toBooking(user, item, bookingDto);
        return BookingMapper.toBookingOut(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDtoOut update(Long userId, Long bookingId, Boolean approved) {
        Booking booking = validateBookingDetails(userId, bookingId, 1);
        assert booking != null;
        Status newStatus = approved ? Status.APPROVED : Status.REJECTED;
        booking.setStatus(newStatus);
        return BookingMapper.toBookingOut(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoOut findBookingByUserId(Long userId, Long bookingId) {
        Booking booking = validateBookingDetails(userId, bookingId, 2);
        assert booking != null;
        return BookingMapper.toBookingOut(booking);
    }

    @Override
    @Transactional
    public List<BookingDtoOut> findAllForBooker(Long bookerId, String state) {
        userService.findById(bookerId);
        switch (validState(state)) {
            case ALL:
                return bookingRepository.findAllBookingsByBookerId(bookerId).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllCurrentBookingsByBookerId(bookerId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case PAST:
                return bookingRepository.findAllPastBookingsByBookerId(bookerId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case FUTURE:
                return bookingRepository.findAllFutureBookingsByBookerId(bookerId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case WAITING:
                return bookingRepository.findAllWaitingBookingsByBookerId(bookerId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case REJECTED:
                return bookingRepository.findAllRejectedBookingsByBookerId(bookerId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());
            default:
                throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    @Transactional
    public List<BookingDtoOut> findAllForOwner(Long ownerId, String state) {
        userService.findById(ownerId);
        switch (validState(state)) {
            case ALL:
                return bookingRepository.findAllBookingsByOwnerId(ownerId).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllCurrentBookingsByOwnerId(ownerId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case PAST:
                return bookingRepository.findAllPastBookingsByOwnerId(ownerId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case FUTURE:
                return bookingRepository.findAllFutureBookingsByOwnerId(ownerId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case WAITING:
                return bookingRepository.findAllWaitingBookingsByOwnerId(ownerId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case REJECTED:
                return bookingRepository.findAllRejectedBookingsByOwnerId(ownerId).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());
            default:
                throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
    }


    private void bookingValidation(BookingDto bookingDto, User user, Item item) {
        if (!item.getAvailable()) {
            throw new ValidationException("The item is not available for booking");
        }
        if (user.getId().equals(item.getOwner().getId())) {
            throw new NotFoundException("The item was not found");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new ValidationException("The end date cannot be earlier than or equal to the start date");
        }
    }

    private State validState(String bookingState) {
        State state = State.from(bookingState);
        if (state == null) {
            throw new IllegalArgumentException("Unknown state: " + bookingState);
        }
        return state;
    }

    private Booking validateBookingDetails(Long userId, Long bookingId, Integer number) {
        Optional<Booking> bookingById = bookingRepository.findById(bookingId);
        if (bookingById.isEmpty()) {
            throw new NotFoundException("The booking was not found");
        }
        Booking booking = bookingById.get();
        switch (number) {
            case 1:
                if (!booking.getItem().getOwner().getId().equals(userId)) {
                    throw new NotFoundException("The user is not the owner");
                }
                if (!booking.getStatus().equals(Status.WAITING)) {
                    throw new ValidationException("Booking with the WAITING status");
                }
                return booking;
            case 2:
                if (!booking.getBooker().getId().equals(userId)
                        && !booking.getItem().getOwner().getId().equals(userId)) {
                    throw new NotFoundException("The user is not the owner or the author of the reservation");
                }
                return booking;
        }
        return null;
    }
}