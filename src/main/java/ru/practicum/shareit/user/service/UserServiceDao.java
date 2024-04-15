package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserServiceDao {
    User createUser(User user);

    User updateUser(Long id, User user);

    User findUserById(Long id);

    void deleteUser(Long id);

    List<User> findAllUsers();
}
