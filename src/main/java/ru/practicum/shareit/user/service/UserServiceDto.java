package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserServiceDto {
    UserDto createUserDto(UserDto userDto);

    UserDto updateUserDto(Long id, UserDto userDto);

    UserDto findUserDtoById(Long id);

    void deleteUserDto(Long id);

    List<UserDto> getAllUserDto();

}
