package com.cdm.services;

import com.cdm.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User saveUser(User user);

    Optional<User> getUserById(String userId);

    Optional<User> updateUser(User user);

    User getUserByEmail(String email);

    User getUserByToken(String token);

    void deleteUser(String userId);

    boolean isUserExist(String userId);

    boolean isUserExistByEmail(String email);

    List<User> getAllUser();

    // Add more methods related to user business logic
}
