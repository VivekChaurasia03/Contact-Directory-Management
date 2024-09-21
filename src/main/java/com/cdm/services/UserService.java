package com.cdm.services;

import com.cdm.entities.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User saveUser(User user, HttpSession session);

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
