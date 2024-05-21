package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.markers.Create;
import ru.practicum.shareit.user.markers.Update;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto add(@Validated({Create.class}) @RequestBody UserDto userDto) {
        log.info("Request to add a user {}", userDto);
        return userService.add(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId, @Validated({Update.class}) @RequestBody UserDto userDto) {
        log.info("Request to update a user with id= {}", userId);
        return userService.update(userId, userDto);
    }

    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable Long userId) {
        log.info("Request to get a user with id= {}", userId);
        return userService.findById(userId);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.info("Request to delete a user with id= {}", userId);
        userService.delete(userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Request to get all users");
        return userService.getAllUsers();
    }


}
