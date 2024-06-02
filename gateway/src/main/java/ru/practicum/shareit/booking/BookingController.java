
package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exceptions.ValidationException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(name = "state", defaultValue = "ALL") String searchMode,
                                         @RequestParam(required = false) Integer from,
                                         @RequestParam(required = false) Integer size) {

        if ((from != null && from <= 0) || (size != null && size <= 0)) {
            throw new IllegalArgumentException("Failed to process request. Incorrect pagination parameters.");
        }

        if (from == null) {
            from = 0;
        }

        if (size == null) {
            size = Integer.MAX_VALUE;
        }

        BookingState bookingSearchMode;

        try {
            bookingSearchMode = BookingState.valueOf(searchMode.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new ValidationException("Unknown state: " + searchMode);
        }

        log.info("Get booking with searchMode={}, userId={}, from={}, size={}", searchMode, userId, from, size);

        return bookingClient.getAll(userId, bookingSearchMode, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestBody @Valid BookItemRequestDto requestDto) {

        log.info("Creating booking {}, userId={}", requestDto, userId);

        return bookingClient.add(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable Long bookingId) {

        log.info("Get bookingId={}, userId={}", bookingId, userId);

        return bookingClient.get(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> setApprove(@PathVariable Long bookingId,
                                             @RequestHeader("X-Sharer-User-Id") @NotNull Long ownerId,
                                             @RequestParam("approved") @NotNull Boolean approved) {

        log.info("Set approve bookingId={}, userId={}, approved={}", bookingId, ownerId, approved);

        return bookingClient.setApprove(bookingId, ownerId, approved);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(@RequestParam(name = "state", defaultValue = "ALL", required = false)
                                                    String searchMode,
                                                @RequestParam(required = false) @Positive Integer from,
                                                @RequestParam(required = false) @Positive Integer size,
                                                @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {

        if (from == null) {
            from = 0;
        }

        if (size == null) {
            size = Integer.MAX_VALUE;
        }

        BookingState bookingSearchMode;

        try {
            bookingSearchMode = BookingState.valueOf(searchMode.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new ValidationException("Unknown state: " + searchMode);
        }

        log.info("Get all bookings by userId={}, searchMode={}, from={}, size={}", userId, searchMode, from, size);

        return bookingClient.getAllByOwner(userId, bookingSearchMode, from, size);
    }
}
