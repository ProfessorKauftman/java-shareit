package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {
    public Booking toBooking(User user, Item item, BookingDto bookingDto) {
        return new Booking(
                item,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                user,
                Status.WAITING);
    }

    public BookingDtoOut toBookingOut(Booking booking) {
        return new BookingDtoOut(
                booking.getId(),
                ItemMapper.toItemDtoOut(booking.getItem()),
                booking.getStart(),
                booking.getEnd(),
                UserMapper.toUserDto(booking.getBooker()),
                booking.getStatus());
    }

}
