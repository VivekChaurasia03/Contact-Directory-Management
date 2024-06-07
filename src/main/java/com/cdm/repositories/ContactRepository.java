package com.cdm.repositories;

import com.cdm.entities.Contact;
import com.cdm.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, String> {

    // Custom finder method - Find Contacts by User
    Optional<List<Contact>> findByUser(User user);

    // Custom finder method - Find Contacts by User - For Pagination
    Optional<Page<Contact>> findByUser(User user, Pageable pageable);

    // Custom Query method - Find Contacts by UserId
    @Query("SELECT c from Contact c where c.user.userId = :userId")
    Optional<List<Contact>> findByUserId(@Param("userId") String userId);
}
