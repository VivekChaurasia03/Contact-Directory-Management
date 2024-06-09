package com.cdm.services.implmentation;

import com.cdm.entities.Contact;
import com.cdm.entities.User;
import com.cdm.helpers.AppConstants;
import com.cdm.helpers.ResourceNotFoundException;
import com.cdm.repositories.ContactRepository;
import com.cdm.services.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ContactServiceImplementation implements ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Override
    public Contact saveContact(Contact contact) {

        String contactId = UUID.randomUUID().toString();
        contact.setId(contactId);
        return contactRepository.save(contact);
    }

    @Override
    public Optional<Contact> updateContact(Contact contact) {
        Contact foundedContact = contactRepository.findById(contact.getId()).orElseThrow(() -> new ResourceNotFoundException("Contact Not Found"));
        foundedContact.setName(contact.getName());
        foundedContact.setEmail(contact.getEmail());
        foundedContact.setPhoneNumber(contact.getPhoneNumber());
        foundedContact.setAddress(contact.getAddress());
        foundedContact.setDescription(contact.getDescription());
        foundedContact.setFavourite(contact.isFavourite());
        foundedContact.setWebsiteLink(contact.getWebsiteLink());
        foundedContact.setLinkedInLink(contact.getLinkedInLink());


        // Save the contact
        return Optional.of(contactRepository.save(foundedContact));
    }

    @Override
    public Optional<Contact> getContactById(String contactId) {
        return Optional.ofNullable(contactRepository.findById(contactId).orElseThrow(() -> new ResourceNotFoundException("Contact not found with given Id " + contactId)));
    }

    @Override
    public Optional<List<Contact>> getContactByUser(User user) {
        return contactRepository.findByUser(user);
    }

    // Method overloading for pagination
    @Override
    public Optional<Page<Contact>> getContactByUser(User user, int page, int size, String sortField, String direction) {
        Sort sort = direction.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        var pageable = PageRequest.of(page, size, sort);
        return contactRepository.findByUser(user, pageable);
    }

    @Override
    public Optional<List<Contact>> getContactByUserId(String userId) {
        return contactRepository.findByUserId(userId);
    }

    @Override
    public void deleteContact(String contactId) {
        Contact foundedContact = contactRepository.findById(contactId).orElseThrow(() -> new ResourceNotFoundException("Contact not found with given Id " + contactId));
        contactRepository.delete(foundedContact);
    }

    @Override
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    @Override
    public Optional<Page<Contact>> searchByName(User user, String nameKeyword, int page, int size, String sortField, String direction) {
        Sort sort = direction.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        var pageable = PageRequest.of(page, size, sort);
        return contactRepository.findByUserAndNameContaining(user, nameKeyword, pageable);
    }

    @Override
    public Optional<Page<Contact>> searchByEmail(User user, String emailKeyword, int page, int size, String sortField, String direction) {
        Sort sort = direction.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        var pageable = PageRequest.of(page, size, sort);
        return contactRepository.findByUserAndEmailContaining(user, emailKeyword, pageable);
    }

    @Override
    public Optional<Page<Contact>> searchByPhoneNumber(User user, String phoneNumberKeyword, int page, int size, String sortField, String direction) {
        Sort sort = direction.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        var pageable = PageRequest.of(page, size, sort);
        return contactRepository.findByUserAndPhoneNumberContaining(user, phoneNumberKeyword, pageable);
    }
}
