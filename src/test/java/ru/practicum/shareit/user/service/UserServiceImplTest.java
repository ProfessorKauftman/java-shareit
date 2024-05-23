package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("Professor")
            .email("professor@yandex.ru")
            .build();

    @Test
    void whenAddNewUserReturnUserDto() {
        User userForSaving = User.builder()
                .id(1L)
                .name("Professor")
                .email("professor@yandex.ru")
                .build();
        when(userRepository.save(userForSaving)).thenReturn(userForSaving);

        UserDto realUser = userService.add(userDto);

        assertEquals(userDto, realUser);
        verify(userRepository).save(userForSaving);
    }

    @Test
    void whenUpdateUserEverythingIsOk() {
        UserDto user = userService.add(userDto);
        Long userId = user.getId();

        UserDto updatedFields = new UserDto();
        updatedFields.setName("Updated Professor");
        updatedFields.setEmail("updatedProfessor@yandex.ru");
        when(userRepository.findById(userId)).thenReturn(Optional.of(UserMapper.toUser(user)));
        UserDto updatedUser = userService.update(userId, updatedFields);
        assertNotNull(updatedUser);
        assertEquals("Updated Professor", updatedUser.getName());
        assertEquals("updatedProfessor@yandex.ru", updatedUser.getEmail());

    }

    @Test
    void whenFindUserByIdAndEverythingIsOk() {
        long userId = 1L;
        User user = User.builder()
                .id(1L)
                .name("Professor")
                .email("professor@yandex.ru")
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        UserDto userDto = UserMapper.toUserDto(user);

        UserDto realUserDto = userService.findById(userId);

        assertEquals(userDto, realUserDto);
    }

    @Test
    void whenFindUserByIdAndNotFound() {
        long userId = 0L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException userNotFound = assertThrows(NotFoundException.class,
                () -> userService.findById(userId));

        assertEquals(userNotFound.getMessage(), "User with id= " + userId + " doesn't exist.");
    }

    @Test
    void whenFindAllUsers() {
        List<User> users = List.of(new User());
        List<UserDto> usersDto = users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());

        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> realUsersDto = userService.getAllUsers();

        assertEquals(realUsersDto.size(), 1);
        assertEquals(realUsersDto, usersDto);
    }

    @Test
    void whenDeleteUser() {
        long userId = 0L;
        userService.delete(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }
}