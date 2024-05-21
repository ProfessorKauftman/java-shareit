package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @SneakyThrows
    void whenCreateUserAndHeIsValid() {
        UserDto userDto = UserDto.builder()
                .name("Professor")
                .email("professor@yandex.ru")
                .build();

        when(userService.add(userDto)).thenReturn(userDto);

        String result = mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);
    }

    @Test
    @SneakyThrows
    void whenCreateUserAndEmailIsNotValidShouldReturnBadRequest() {
        UserDto userDto = UserDto.builder()
                .name("Professor")
                .email("professor.ru")
                .build();

        when(userService.add(userDto)).thenReturn(userDto);

        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).add(userDto);
    }

    @Test
    @SneakyThrows
    void whenCreateUserAndNameIsNotaValidShouldReturnBadRequest() {
        UserDto userDto = UserDto.builder()
                .name(" ")
                .email("professor@yandex.ru")
                .build();

        when(userService.add(userDto)).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).add(userDto);
    }

    @Test
    @SneakyThrows
    void whenUpdateUserAndEverythingIsOk() {
        long userId = 0L;
        UserDto userDto = UserDto.builder()
                .name("UpdateProfessor")
                .email("updateProfessor@yandex.ru")
                .build();

        when(userService.update(userId, userDto)).thenReturn(userDto);

        String result = mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);
    }

    @Test
    @SneakyThrows
    void whenUpdateUserEmailButNotValidShouldReturnBadRequest() {
        long userId = 0L;
        UserDto userDto = UserDto.builder()
                .name("UpdateProfessor")
                .email("updateProfessor.ru")
                .build();

        when(userService.update(userId, userDto)).thenReturn(userDto);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).update(userId, userDto);
    }

    @Test
    @SneakyThrows
    void whenGetUserByIdAndEverythingIsOk() {
        long userId = 0L;

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).findById(userId);
    }

    @Test
    @SneakyThrows
    void whenGetAllUsersAndEverythingIsOk() {
        List<UserDto> userDtoList = List.of(
                UserDto.builder()
                        .name("Professor")
                        .email("professor@yandex.ru")
                        .build());

        when(userService.getAllUsers()).thenReturn(userDtoList);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDtoList), result);
    }

    @Test
    @SneakyThrows
    void whenDeleteUserAndEverythingIsOk() {
        long userId = 0L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService, times(1)).delete(userId);
    }

}