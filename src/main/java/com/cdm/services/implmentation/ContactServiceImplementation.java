package com.cdm.services.implmentation;

import com.cdm.entities.Contact;
import com.cdm.entities.User;
import com.cdm.helpers.AppConstants;
import com.cdm.helpers.ResourceNotFoundException;
import com.cdm.repositories.ContactRepository;
import com.cdm.services.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Contact> search(String name, String email, String phoneNumber) {
        return null;
    }
}
