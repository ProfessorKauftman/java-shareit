package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceDtoImpl;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserServiceDtoImpl userServiceDtoImpl;

    @PostMapping
    public UserDto createUserDto(@Valid @RequestBody UserDto userDto) {
        return userServiceDtoImpl.createUserDto(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUserDto(@PathVariable Long userId, @RequestBody UserDto userDto) {
        return userServiceDtoImpl.updateUserDto(userId, userDto);
    }

    @GetMapping("/{userId}")
    public UserDto findUserDtoById(@PathVariable Long userId) {
        return userServiceDtoImpl.findUserDtoById(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserDto(@PathVariable Long userId) {
        userServiceDtoImpl.deleteUserDto(userId);
    }

    @GetMapping
    public List<UserDto> getAllUserDto() {
        return userServiceDtoImpl.getAllUserDto();
    }


}
