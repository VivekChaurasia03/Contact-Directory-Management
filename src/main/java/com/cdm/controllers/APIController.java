package com.cdm.controllers;

import com.cdm.entities.Contact;
import com.cdm.services.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class APIController {

    private final ContactService contactService;

    @Autowired
    public APIController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping("/contacts/{id}")
    public Optional<Contact> getContact(@PathVariable String id) {
        return contactService.getContactById(id);
    }
}
