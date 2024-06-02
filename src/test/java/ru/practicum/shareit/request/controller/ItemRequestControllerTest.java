package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.item.controller.ItemController.USER_HEADER;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ItemRequestService itemRequestService;

    private Clock fixed = Clock.fixed(Instant.parse("2024-05-23T12:34:56.789Z"), ZoneOffset.UTC);

    private final User user = User.builder()
            .id(1L)
            .name("Professor")
            .email("professor@yandex.ru")
            .build();

    private final ItemRequestDtoOut requestDtoOut = ItemRequestDtoOut.builder()
            .id(1L)
            .description("Description")
            .created(LocalDateTime.now(fixed))
            .items(List.of())
            .build();


    @Test
    @SneakyThrows
    void whenCreateRequestIsOk() {
        when(itemRequestService.add(any(), any())).thenReturn(requestDtoOut);

        String result = mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                        .content(objectMapper.writeValueAsString(requestDtoOut))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType("application/json")
                        .header(USER_HEADER, user.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("{"
                + "\"id\":1,"
                + "\"description\":\"Description\","
                + "\"created\":\"2024-05-23T12:34:56\","
                + "\"items\":[]"
                + "}", result);
    }

    @Test
    @SneakyThrows
    void whenGetUserRequestIsOk() {
        when(itemRequestService.getUserRequests(user.getId())).thenReturn(List.of(requestDtoOut));

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType("application/json")
                        .header(USER_HEADER, user.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("[{"
                + "\"id\":1,"
                + "\"description\":\"Description\","
                + "\"created\":\"2024-05-23T12:34:56\","
                + "\"items\":[]"
                + "}]", result);
    }

    @Test
    @SneakyThrows
    void whenGetAllRequestsIsOk() {
        int from = 0;
        int size = 10;

        when(itemRequestService.getAllRequests(user.getId(), from, size)).thenReturn(List.of(requestDtoOut));

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType("application/json")
                        .header(USER_HEADER, user.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("[{"
                + "\"id\":1,"
                + "\"description\":\"Description\","
                + "\"created\":\"2024-05-23T12:34:56\","
                + "\"items\":[]"
                + "}]", result);
    }

    @Test
    @SneakyThrows
    void whenGetRequestByIdIsOk() {
        long requestId = 1L;

        when(itemRequestService.getRequestById(user.getId(), requestId)).thenReturn(requestDtoOut);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/requests/{requestId}", requestId)

                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType("application/json")
                        .header(USER_HEADER, user.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("{"
                + "\"id\":1,"
                + "\"description\":\"Description\","
                + "\"created\":\"2024-05-23T12:34:56\","
                + "\"items\":[]"
                + "}", result);
    }

}