package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.setvice.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.shareit.item.controller.ItemController.USER_HEADER;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
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
                                @PathVariable Long bookingId,
                                @RequestParam Boolean approved) {
        log.info("PATCH-request to update the booking status of an item from the owner with id: {}", userId);
        return bookingService.update(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOut findBookingById(@RequestHeader(USER_HEADER) Long userId,
                                         @PathVariable Long bookingId) {
        log.info("GET-request to receive booking data from a user with id: {}", userId);
        return bookingService.findBookingByUserId(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoOut> findAll(@RequestHeader(USER_HEADER) Long userId,
                                       @RequestParam(value = "state", defaultValue = "ALL") String bookingStatus,
                                       @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                       @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        log.info("GET-request to receive a list of all bookings of the current user with id: {} and status {}",
                userId, bookingStatus);
        return bookingService.findAllForBooker(userId, bookingStatus, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoOut> findAllByOwner(@RequestHeader(USER_HEADER) Long userId,
                                              @RequestParam(value = "state",
                                                      defaultValue = "ALL") String bookingStatus,
                                              @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                              @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        log.info("GET-request to receive a list of all bookings of the current owner with id: {} and status {}",
                userId, bookingStatus);
        return bookingService.findAllForOwner(userId, bookingStatus, from, size);
    }
}
