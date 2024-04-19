package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NotUniqueEmailException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserServiceDaoImpl implements UserServiceDao {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private Long generatorId = 1L;

    @Override
    public User createUser(User user) {
        checkEmailExists(user.getEmail());
        user.setId(generatorId);
        users.put(generatorId, user);
        emails.add(user.getEmail());
        generatorId++;
        return user;
    }

    @Override
    public User updateUser(Long id, User user) {
        checkUserExists(id);
        updateEmail(findUserById(id).getEmail(), user.getEmail());
        users.put(id, user);
        return users.get(id);
    }

    @Override
    public User findUserById(Long id) {
        checkUserExists(id);
        return users.get(id);
    }

    @Override
    public void deleteUser(Long id) {
        checkUserExists(id);
        emails.remove(findUserById(id).getEmail());
        users.remove(id);
    }

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    private void checkUserExists(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("User with id: " + id + " doesn't exist");
        }
    }

    private void checkEmailExists(String email) {
        if (emails.contains(email)) {
            throw new NotUniqueEmailException("User with this email: " + email + " exists");
        }
    }

    private void updateEmail(String oldEmail, String newEmail) {
        if (!oldEmail.equals(newEmail)) {
            checkEmailExists(newEmail);
            emails.remove(oldEmail);
            emails.add(newEmail);
        }

    }

    @Override
    public boolean isEmailTaken(String email) {
        return emails.contains(email);
    }

    @Override
    public User findUserByEmail(String email) {
        return users.values().stream()
                .filter(user -> email.equals(user.getEmail()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("User with email: " + email + " doesn't exist"));
    }


}
