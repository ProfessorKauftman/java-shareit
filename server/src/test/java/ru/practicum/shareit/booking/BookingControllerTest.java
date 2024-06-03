package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.item.ItemController.USER_HEADER;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookingService bookingService;
    private final User user = User.builder()
            .id(1L)
            .name("Professor")
            .email("professor@yandex.ru")
            .build();

    private final Item item = Item.builder()
            .id(1L)
            .name("Lopata")
            .description("Lopata description")
            .owner(user)
            .build();

    BookingDtoOut bookingDtoOut = BookingDtoOut.builder()
            .id(1L)
            .start(LocalDateTime.of(2024, 05, 23, 12, 34, 56).plusDays(1L))
            .end(LocalDateTime.of(2024, 05, 23, 12, 34, 56).plusDays(2L))
            .status(BookingStatus.WAITING)
            .item(ItemMapper.toItemDtoOut(item))
            .booker(UserMapper.toUserDto(user))
            .build();


    @Test
    @SneakyThrows
    void whenCreateBookingIsOk() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1L))
                .end(LocalDateTime.now().plusDays(2L))
                .build();

        when(bookingService.add(user.getId(), bookingDto)).thenReturn(bookingDtoOut);

        String result = mockMvc.perform(post("/bookings")
                        .contentType("application/json")
                        .header(USER_HEADER, user.getId())
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("{"
                        + "\"id\":1,"
                        + "\"item\":{"
                        + "\"id\":1,"
                        + "\"name\":\"Lopata\","
                        + "\"description\":\"Lopata description\","
                        + "\"available\":null,"
                        + "\"lastBooking\":null,"
                        + "\"comments\":null,"
                        + "\"nextBooking\":null,"
                        + "\"requestId\":null},"
                        + "\"start\":\"2024-05-24T12:34:56\","
                        + "\"end\":\"2024-05-25T12:34:56\","
                        + "\"booker\":{"
                        + "\"id\":1,"
                        + "\"name\":\"Professor\","
                        + "\"email\":\"professor@yandex.ru\"},"
                        + "\"status\":\"WAITING\","
                        + "\"bookerId\":1,"
                        + "\"itemId\":1"
                        + "}",
                result);
    }

    @Test
    @SneakyThrows
    void whenUpdateBookingIsOk() {
        boolean approved = true;
        long bookingId = 1L;

        when(bookingService.update(user.getId(), bookingId, approved)).thenReturn(bookingDtoOut);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .contentType("application/json")
                        .header(USER_HEADER, user.getId())
                        .param("approved", String.valueOf(approved)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("{"
                        + "\"id\":1,"
                        + "\"item\":{"
                        + "\"id\":1,"
                        + "\"name\":\"Lopata\","
                        + "\"description\":\"Lopata description\","
                        + "\"available\":null,"
                        + "\"lastBooking\":null,"
                        + "\"comments\":null,"
                        + "\"nextBooking\":null,"
                        + "\"requestId\":null"
                        + "},"
                        + "\"start\":\"2024-05-24T12:34:56\","
                        + "\"end\":\"2024-05-25T12:34:56\","
                        + "\"booker\":{"
                        + "\"id\":1,"
                        + "\"name\":\"Professor\","
                        + "\"email\":\"professor@yandex.ru\""
                        + "},"
                        + "\"status\":\"WAITING\","
                        + "\"bookerId\":1,"
                        + "\"itemId\":1"
                        + "}",
                result);
    }

    @Test
    @SneakyThrows
    void whenGetBookingByIdIsOk() {
        long bookingId = 1L;

        when(bookingService.findBookingByUserId(user.getId(), bookingId)).thenReturn(bookingDtoOut);

        String result = mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(USER_HEADER, user.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("{"
                        + "\"id\":1,"
                        + "\"item\":{"
                        + "\"id\":1,"
                        + "\"name\":\"Lopata\","
                        + "\"description\":\"Lopata description\","
                        + "\"available\":null,"
                        + "\"lastBooking\":null,"
                        + "\"comments\":null,"
                        + "\"nextBooking\":null,"
                        + "\"requestId\":null"
                        + "},"
                        + "\"start\":\"2024-05-24T12:34:56\","
                        + "\"end\":\"2024-05-25T12:34:56\","
                        + "\"booker\":{"
                        + "\"id\":1,"
                        + "\"name\":\"Professor\","
                        + "\"email\":\"professor@yandex.ru\""
                        + "},"
                        + "\"status\":\"WAITING\","
                        + "\"bookerId\":1,"
                        + "\"itemId\":1"
                        + "}",
                result);
    }

    @Test
    @SneakyThrows
    void whenGetAllBookingsShouldReturnStatusOk() {
        int from = 0;
        int size = 10;
        String state = "ALL";

        when(bookingService.findAll(user.getId(), BookingState.ALL.toString(), 0, 10))
                .thenReturn(List.of(bookingDtoOut));

        String result = mockMvc.perform(get("/bookings")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .param("state", state)
                        .header(USER_HEADER, user.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("[{"
                        + "\"id\":1,"
                        + "\"item\":{"
                        + "\"id\":1,"
                        + "\"name\":\"Lopata\","
                        + "\"description\":\"Lopata description\","
                        + "\"available\":null,"
                        + "\"lastBooking\":null,"
                        + "\"comments\":null,"
                        + "\"nextBooking\":null,"
                        + "\"requestId\":null"
                        + "},"
                        + "\"start\":\"2024-05-24T12:34:56\","
                        + "\"end\":\"2024-05-25T12:34:56\","
                        + "\"booker\":{"
                        + "\"id\":1,"
                        + "\"name\":\"Professor\","
                        + "\"email\":\"professor@yandex.ru\""
                        + "},"
                        + "\"status\":\"WAITING\","
                        + "\"bookerId\":1,"
                        + "\"itemId\":1"
                        + "}]",
                result);
    }


    @Test
    @SneakyThrows
    void whenGetAllByOwnerIsOk() {
        int from = 0;
        int size = 10;
        String state = "ALL";

        when(bookingService.findAllOwner(user.getId(), BookingState.ALL.toString(), 0, 10))
                .thenReturn(List.of(bookingDtoOut));

        String result = mockMvc.perform(get("/bookings/owner")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .param("state", state)
                        .header(USER_HEADER, user.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("[{"
                        + "\"id\":1,"
                        + "\"item\":{"
                        + "\"id\":1,"
                        + "\"name\":\"Lopata\","
                        + "\"description\":\"Lopata description\","
                        + "\"available\":null,"
                        + "\"lastBooking\":null,"
                        + "\"comments\":null,"
                        + "\"nextBooking\":null,"
                        + "\"requestId\":null"
                        + "},"
                        + "\"start\":\"2024-05-24T12:34:56\","
                        + "\"end\":\"2024-05-25T12:34:56\","
                        + "\"booker\":{"
                        + "\"id\":1,"
                        + "\"name\":\"Professor\","
                        + "\"email\":\"professor@yandex.ru\""
                        + "},"
                        + "\"status\":\"WAITING\","
                        + "\"bookerId\":1,"
                        + "\"itemId\":1"
                        + "}]",
                result);

    }
}