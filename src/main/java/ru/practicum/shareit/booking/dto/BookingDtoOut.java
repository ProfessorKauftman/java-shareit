package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@JsonPropertyOrder({ "id", "item", "start", "end", "booker", "status", "bookerId", "itemId" })
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDtoOut {
    private Long id;
    private ItemDtoOut item;
    private LocalDateTime start;
    private LocalDateTime end;
    private UserDto booker;
    private Status status;

    public Long getItemId() {
        return item.getId();
    }

    public Long getBookerId() {
        return booker.getId();
    }
}
