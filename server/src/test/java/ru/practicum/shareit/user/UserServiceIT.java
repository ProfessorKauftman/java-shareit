package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserServiceIT {
    @Autowired
    UserService userService;

    private final UserDto userDto = UserDto.builder()
            .name("professor")
            .email("professor@yandex.ru")
            .build();

    @Test
    void addNewUser() {
        UserDto actualUserDto = userService.add(userDto);

        assertEquals(8L, actualUserDto.getId());
        assertEquals("professor", actualUserDto.getName());
        assertEquals("professor@yandex.ru", actualUserDto.getEmail());
    }

    @Test
    void getUserByIdWhenUserIdIsNotValid() {
        Long userId = 2L;

        Assertions
                .assertThrows(NotFoundException.class, () -> userService.findById(userId));
    }
}