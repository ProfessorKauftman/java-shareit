package ru.practicum.shareit.request.integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestIT {

    @Autowired
    ItemRequestService itemRequestService;

    @Autowired
    UserService userService;

    private final UserDto userDto = UserDto.builder()
            .name("Professor")
            .email("professor@yandex.ru")
            .build();

    private final ItemRequestDto requestDto = ItemRequestDto.builder()
            .description("Description")
            .build();

    @Test
    void whenAddNewRequestIsOk() {
        UserDto user = userService.add(userDto);
        itemRequestService.add(user.getId(), requestDto);

        List<ItemRequestDtoOut> requests = itemRequestService.getUserRequests(user.getId());

        assertEquals("Description", requests.get(0).getDescription());
    }

    @Test
    void whenGetRequestByIdIsNotValidShouldThrowNotFoundException() {
        long requestId = 2L;

        Assertions.assertThrows(RuntimeException.class,
                () -> itemRequestService.getRequestById(userDto.getId(), requestId));
    }
}
