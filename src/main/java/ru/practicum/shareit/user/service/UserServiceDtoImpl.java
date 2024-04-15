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
    public UserDto updateUserDto(Long id, UserDto userDto) {
        if (!checkUserDtoExists(id)) {
            throw new NotFoundException("User with id: " + id + " doesn't exist");
        }
        User user = new User();
        UserDto forMemory = findUserDtoById(id);
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        } else {
            user.setName(forMemory.getName());
        }
        if (userDto.getEmail() != null) {
            checkEmailExists(user);
            user.setEmail(userDto.getEmail());
        } else {
            user.setEmail(forMemory.getEmail());
        }
        user.setId(id);
        return UserMapper.userToDto(userServiceDao.updateUser(id, user));
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
        boolean isEmailNotUnique = userServiceDao.findAllUsers().stream()
                .anyMatch(thisUser -> thisUser.getEmail().equals(user.getEmail())
                        && !thisUser.getId().equals(user.getId()));
        if (isEmailNotUnique) {
            throw new NotUniqueEmailException("User with this email already exists");
        }
    }

    private boolean checkUserDtoExists(Long userId) {
        return userServiceDao.findAllUsers().stream()
                .anyMatch(user -> user.getId().equals(userId));
    }
}
