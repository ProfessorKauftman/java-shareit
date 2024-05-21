package ru.practicum.shareit.booking.integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.setvice.BookingService;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingServiceIT {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    private final UserDto userDto1 = UserDto.builder()
            .name("Professor")
            .email("professor@yandesx.ru")
            .build();

    private final UserDto userDto2 = UserDto.builder()
            .name("Ne_Professor")
            .email("NeProfessor@yandesx.ru")
            .build();

    private final ItemDto itemDot1 = ItemDto.builder()
            .name("Lopata")
            .description("Lopata description")
            .available(true)
            .build();

    private final ItemDto itemDot2 = ItemDto.builder()
            .name("Otvertka")
            .description("Otvertka description")
            .available(true)
            .build();

    private final BookingDto bookingDto = BookingDto.builder()
            .itemId(2L)
            .start(LocalDateTime.now().plusSeconds(10L))
            .end(LocalDateTime.now().plusSeconds(15L))
            .build();


    @Test
    void whenAddBookingIsOk() {
        UserDto addUserDto1 = userService.add(userDto1);
        UserDto addUserDto2 = userService.add(userDto2);
        itemService.createItemDto(addUserDto1.getId(), itemDot1);
        itemService.createItemDto(addUserDto2.getId(), itemDot2);

        BookingDtoOut bookingDtoOut1 = bookingService.add(addUserDto1.getId(),  bookingDto);
        BookingDtoOut bookingDtoOut2 = bookingService.add(addUserDto1.getId(),  bookingDto);

        assertEquals(1L, bookingDtoOut1.getId());
        assertEquals(2L, bookingDtoOut2.getId());
        assertEquals(Status.WAITING, bookingDtoOut1.getStatus());
        assertEquals(Status.WAITING, bookingDtoOut2.getStatus());

        BookingDtoOut updateBookingDto1 = bookingService
                .update(addUserDto2.getId(), bookingDtoOut1.getId(), true);
        BookingDtoOut updateBookingDto2 = bookingService
                .update(addUserDto2.getId(), bookingDtoOut2.getId(), true);

        assertEquals(Status.APPROVED, updateBookingDto1.getStatus());
        assertEquals(Status.APPROVED, updateBookingDto2.getStatus());

        List<BookingDtoOut> bookingDtoOuts = bookingService
                .findAllForOwner(addUserDto2.getId(), State.ALL.toString(), 0, 10);

        assertEquals(2, bookingDtoOuts.size());
    }

    @Test
    void whenUpdateAndBookingIdAndUserIdAreNotValidShouldThrowNotFoundException() {
        long userId = 3L;
        long bookingId = 3L;

        Assertions.assertThrows(NotFoundException.class, () -> bookingService.update(userId, bookingId, true));
    }
}
