package ru.practicum.shareit.item.integration;


import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.setvice.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceIT {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemRequestService requestService;

    @Autowired
    private BookingService bookingService;

    private final UserDto userDto1 = UserDto.builder()
            .name("Professor")
            .email("professor@yandex.ru")
            .build();

    private final UserDto userDto2 = UserDto.builder()
            .name("Ne_Professor")
            .email("NeProfessor@yandex.ru")
            .build();

    private final ItemDto itemDto1 = ItemDto.builder()
            .name("Lopata")
            .description("Lopata description")
            .available(true)
            .build();

    private final ItemDto itemDto2 = ItemDto.builder()
            .name("Otvertka")
            .description("Otvertka description")
            .available(true)
            .build();

    private final ItemDto itemDtoRequest = ItemDto.builder()
            .name("ItemDtoRequest name")
            .description("ItemDtoRequest description")
            .available(true)
            .requestId(1L)
            .build();

    private final ItemRequestDto requestDto = ItemRequestDto.builder()
            .description("request description")
            .build();

    private final BookingDto bookingDto = BookingDto.builder()
            .itemId(1L)
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusSeconds(1L))
            .build();

    private final CommentDto commentDto = CommentDto.builder()
            .text("comment")
            .build();

    @Test
    void whenAddNewItemIsOk() {
        UserDto user = userService.add(userDto1);
        ItemDtoOut item = itemService.createItemDto(user.getId(), itemDto1);

        assertEquals(1L, item.getId());
        assertEquals("Lopata", item.getName());
    }

    @Test
    @SneakyThrows
    void whenAddCommentIsOk() {
        UserDto user1 = userService.add(userDto1);
        UserDto user2 = userService.add(userDto2);
        ItemDtoOut item = itemService.createItemDto(user2.getId(), itemDto2);
        BookingDtoOut bookingDtoOut = bookingService.add(user1.getId(), bookingDto);

        bookingService.update(user2.getId(), bookingDtoOut.getId(), true);
        Thread.sleep(2000);
        CommentDtoOut commentDtoOut = itemService.createComment(user1.getId(), commentDto, item.getId());

        assertEquals(1L, commentDtoOut.getId());
        assertEquals("comment", commentDtoOut.getText());
    }

    @Test
    void whenAddRequestIsOk() {
        UserDto user = userService.add(userDto1);
        requestService.add(user.getId(), requestDto);

        ItemDtoOut itemRequest = itemService.createItemDto(user.getId(), itemDtoRequest);

        assertEquals(1L, itemRequest.getRequestId());
        assertEquals("ItemDtoRequest name", itemRequest.getName());
    }

    @Test
    void whenGetItemByIdIsNotValidShouldThrowException() {
        long itemId = 3L;

        Assertions.assertThrows(RuntimeException.class, () -> itemService.findItemDtoById(userDto1.getId(), itemId));
    }
}
