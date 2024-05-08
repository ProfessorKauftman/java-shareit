package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.setvice.BookingService;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.item.controller.ItemController.USER_HEADER;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOut add(@RequestHeader(USER_HEADER) Long userId,
                             @Valid @RequestBody BookingDto bookingDto) {
        log.info("POST-request to create a new item booking from a user with id: {}", userId);
        return bookingService.add(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOut update(@RequestHeader(USER_HEADER) Long userId,
                                @PathVariable("bookingId") Long bookingId,
                                @RequestParam(name = "approved") Boolean approved) {
        log.info("PATCH-request to update the booking status of an item from the owner with id: {}", userId);
        return bookingService.update(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOut findBookingById(@RequestHeader(USER_HEADER) Long userId,
                                         @PathVariable("bookingId") Long bookingId) {
        log.info("GET-request to receive booking data from a user with id: {}", userId);
        return bookingService.findBookingByUserId(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoOut> findAll(@RequestHeader(USER_HEADER) Long userId,
                                       @RequestParam(value = "state", defaultValue = "ALL") String bookingStatus) {
        log.info("GET-request to receive a list of all bookings of the current user with id: {} and status {}",
                userId, bookingStatus);
        return bookingService.findAllForBooker(userId, bookingStatus);
    }

    @GetMapping("/owner")
    public List<BookingDtoOut> findAllByOwner(@RequestHeader(USER_HEADER) Long userId,
                                              @RequestParam(value = "state",
                                                      defaultValue = "ALL") String bookingStatus) {
        log.info("GET-request to receive a list of all bookings of the current owner with id: {} and status {}",
                userId, bookingStatus);
        return bookingService.findAllForOwner(userId, bookingStatus);
    }
}
