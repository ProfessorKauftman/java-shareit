
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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.Constants.USER_HEADER;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader(USER_HEADER) long userId,
                                         @RequestParam(name = "state", defaultValue = "ALL") String searchMode,
                                         @PositiveOrZero @RequestParam(defaultValue = "0")
                                         Integer from,
                                         @Positive @RequestParam(defaultValue = "10")
                                         Integer size) {
        BookingState state = BookingState.from(searchMode).orElseThrow(() ->
                new IllegalArgumentException("Unknown state: " + searchMode));
        log.info("Get booking with state {}, userId={}, from={}, size={}", searchMode, userId, from, size);
        return bookingClient.getAll(userId, state, from, size);
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
                                             @RequestParam @NotNull Boolean approved) {

        log.info("Set approve bookingId={}, userId={}, approved={}", bookingId, ownerId, approved);

        return bookingClient.setApprove(bookingId, ownerId, approved);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(@RequestHeader(USER_HEADER) Long ownerId,
                                                @RequestParam(value = "state", defaultValue = "ALL", required = false)
                                                String searchMode,
                                                @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        BookingState state = BookingState.from(searchMode)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + searchMode));
        log.info("Get all bookings by userId={}, searchMode={}, from={}, size={}", searchMode, ownerId, from, size);
        return bookingClient.getAllByOwner(ownerId, state, from, size);
    }
}
