package com.cdm.controllers;

import com.cdm.entities.Contact;
import com.cdm.entities.User;
import com.cdm.forms.ContactForm;
import com.cdm.helpers.EmailHelper;
import com.cdm.helpers.Message;
import com.cdm.helpers.MessageType;
import com.cdm.services.ContactService;
import com.cdm.services.ImageService;
import com.cdm.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.UUID;

@Controller
@RequestMapping("/user/contacts")
public class ContactController {

    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);

    @Autowired
    private ContactService contactService;

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    // Handler to show contact form
    @GetMapping("/add")
    public String addContactView(Model model) {

        ContactForm contactForm = new ContactForm();

        model.addAttribute("contactForm", contactForm);

        return "/user/add_contact";
    }

    @PostMapping("/add")
    public String saveContact(@Valid @ModelAttribute ContactForm contactForm, BindingResult bindingResult, Authentication authentication, HttpSession session) throws IOException {

        // Fetched the form data - ContactForm
        // System.out.println(contactForm);

        // TODO: Validate the data
        if (bindingResult.hasErrors()) {
            Message message = Message.builder().content("Please correct the following error").type(MessageType.red).build();
            session.setAttribute("message", message);
            return "/user/add_contact";
        }

        // Extracted the data from contactForm
        Contact contactToSave = getContactToSave(contactForm, authentication);

        // Save to dB
        contactService.saveContact(contactToSave);

        // message = "Registration Successful"
        Message message = Message.builder().content("Contact Saved Successfully").type(MessageType.green).build();
        session.setAttribute("message", message);

        // Redirect to login page
        return "redirect:/user/contacts/add";
    }

    private Contact getContactToSave(ContactForm contactForm, Authentication authentication) throws IOException {
        // Fetch email loggedIn user information to set the user on the contact saved
        String userEmail = EmailHelper.getEmailOfLoggedInUser(authentication);
        User contactOwner = userService.getUserByEmail(userEmail);

        // Process the contact picture
        String fileName = null;
        String fileURL = null;
        if(!contactForm.getContactImage().isEmpty()) {
            logger.info("File name is {}", contactForm.getContactImage().getOriginalFilename());
            fileName = UUID.randomUUID().toString();
            fileURL = imageService.uploadImage(contactForm.getContactImage(), fileName);
        } else {
            logger.info("No contact image provided.");
        }

        return generateContactToSave(contactForm, fileURL, fileName, contactOwner);
    }

    private static Contact generateContactToSave(ContactForm contactForm, String fileURL, String fileName, User contactOwner) {
        // Contact Object
        Contact contactToSave = new Contact();
        contactToSave.setName(contactForm.getName());
        contactToSave.setEmail(contactForm.getEmail());
        contactToSave.setPhoneNumber(contactForm.getPhoneNumber());
        contactToSave.setAddress(contactForm.getAddress());
        contactToSave.setDescription(contactForm.getDescription());
        contactToSave.setWebsiteLink(contactForm.getWebsiteLink());
        contactToSave.setLinkedInLink(contactForm.getLinkedInLink());
        contactToSave.setFavourite(contactForm.isFavourite());
        contactToSave.setPicture(fileURL);
        contactToSave.setCloudinaryImagePublicId(fileName);
        contactToSave.setUser(contactOwner);

        return contactToSave;
    }
}
