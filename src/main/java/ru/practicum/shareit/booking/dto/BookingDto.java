package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDto {
    @NotNull
    private Long itemId;
    @NotNull
    @Future
    private LocalDateTime end;
    @NotNull
    @FutureOrPresent
    private LocalDateTime start;

}
