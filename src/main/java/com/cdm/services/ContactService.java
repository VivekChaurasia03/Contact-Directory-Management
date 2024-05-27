package com.cdm.services;

import com.cdm.entities.Contact;
import com.cdm.entities.User;

import java.util.List;
import java.util.Optional;

public interface ContactService {

    Contact saveContact(Contact contact);

    Optional<Contact> updateContact(Contact contact);

    Optional<Contact> getContactById(String contactId);

    Optional<List<Contact>> getContactByUser(User user);

    Optional<List<Contact>> getContactByUserId(String userId);

    void deleteContact(String contactId);

    List<Contact> getAllContacts();

    List<Contact> search(String name, String email, String phoneNumber);

    // Add more methods related to user business logic
}
