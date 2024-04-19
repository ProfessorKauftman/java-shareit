package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NotUniqueEmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceDtoImpl implements UserServiceDto {
    private final UserServiceDao userServiceDao;

    @Override
    public UserDto createUserDto(UserDto userDto) {
        User user = UserMapper.dtoToUser(userDto);
        checkEmailExists(user);
        return UserMapper.userToDto(userServiceDao.createUser(user));

    }

    @Override
    public UserDto updateUserDto(Long userId, UserDto userDto) {
        if (!checkUserDtoExists(userId)) {
            throw new NotFoundException("User with id: " + userId + " doesn't exist");
        }
        UserDto forMemory = findUserDtoById(userId);
        String name = userDto.getName() != null ? userDto.getName() : forMemory.getName();
        String email = userDto.getEmail() != null ? userDto.getEmail() : forMemory.getEmail();
        if (userDto.getEmail() != null && !email.equals(forMemory.getEmail())) {
            checkEmailExists(new User(userId, name, email));
        }
        User user = new User(userId, name, email);
        return UserMapper.userToDto(userServiceDao.updateUser(userId, user));
    }

    @Override
    public UserDto findUserDtoById(Long userId) {
        if (!checkUserDtoExists(userId)) {
            throw new NotFoundException("User with id: " + userId + " doesn't exist");
        }
        User user = userServiceDao.findUserById(userId);
        return UserMapper.userToDto(user);
    }

    @Override
    public void deleteUserDto(Long userId) {
        if (!checkUserDtoExists(userId)) {
            throw new NotFoundException("User with id: " + userId + " doesn't exist");
        }
        userServiceDao.deleteUser(userId);
    }

    @Override
    public List<UserDto> getAllUserDto() {
        return userServiceDao.findAllUsers().stream()
                .map(UserMapper::userToDto)
                .collect(Collectors.toList());
    }

    private void checkEmailExists(User user) {
        if (userServiceDao.isEmailTaken(user.getEmail()) &&
                !user.getId().equals(userServiceDao.findUserByEmail(user.getEmail()).getId())) {
            throw new NotUniqueEmailException("User with this email already exists");
        }
    }


    private boolean checkUserDtoExists(Long userId) {
        return userServiceDao.findAllUsers().stream()
                .anyMatch(user -> user.getId().equals(userId));
    }
}
