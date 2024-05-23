package com.cdm.repositories;


import com.cdm.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    // Extra DB related queries
    Optional<User> findByEmail(String email);

    // Custom query finder
    // Custom finder methods
}
