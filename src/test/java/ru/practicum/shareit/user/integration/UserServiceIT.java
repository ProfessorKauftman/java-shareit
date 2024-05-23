package ru.practicum.shareit.user.integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserServiceIT {

    @Autowired
    UserService userService;

    private final UserDto userDto = UserDto.builder()
            .name("Professor")
            .email("professor@yandex.ru")
            .build();

    @Test
    void whenAddNewUserCorrect() {
        UserDto actualUser = userService.add(userDto);

        assertEquals(1L, actualUser.getId());
        assertEquals("Professor", actualUser.getName());
        assertEquals("professor@yandex.ru", actualUser.getEmail());
    }

    @Test
    void whenGetUserWithWrongIdShouldReturnNotFoundException() {
        long userId = 2L;

        Assertions.assertThrows(NotFoundException.class, () -> userService.findById(userId));
    }

    @Test
    void whenUpdateUserThenUserDataShouldBeUpdated() {
        UserDto createdUser = userService.add(userDto);

        UserDto updateUserDto = UserDto.builder()
                .id(createdUser.getId())
                .name("Updated Professor")
                .email("updatedProfessor@yandex.ru")
                .build();

        UserDto updatedUser = userService.update(createdUser.getId(), updateUserDto);

        assertNotNull(updatedUser);
        assertEquals(createdUser.getId(), updatedUser.getId());
        assertEquals(updateUserDto.getName(), updatedUser.getName());
        assertEquals(updateUserDto.getEmail(), updatedUser.getEmail());

    }

}
