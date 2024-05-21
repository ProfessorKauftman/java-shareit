package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    private final Booking booking = Booking.builder()
            .id(1L)
            .booker(User.builder()
                    .id(1L)
                    .name("Professor")
                    .email("professor@yandex.ru")
                    .build())
            .item(new Item())
            .start(LocalDateTime.now().plusMinutes(5))
            .end(LocalDateTime.now().plusMinutes(10))
            .build();

    @Test
    void whenToBookingItemDtoIsOk() {
        BookingItemDto bookingItemDto = BookingMapper.toBookingItemDto(booking);

        assertEquals(1L, bookingItemDto.getId());
        assertEquals(1L, bookingItemDto.getBookerId());
    }

}