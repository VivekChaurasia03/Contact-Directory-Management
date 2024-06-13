package com.cdm.controllers;

import com.cdm.entities.Contact;
import com.cdm.entities.User;
import com.cdm.forms.ContactForm;
import com.cdm.forms.ContactSearchForm;
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
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static com.cdm.helpers.AppConstants.PAGE_SIZE;

@Controller
@RequestMapping("/user/contacts")
public class ContactController {

    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);

    private final ContactService contactService;
    private final UserService userService;
    private final ImageService imageService;

    @Autowired
    public ContactController(ContactService contactService, UserService userService, ImageService imageService) {
        this.contactService = contactService;
        this.userService = userService;
        this.imageService = imageService;
    }

    @GetMapping("/add")
    public String addContactView(Model model) {
        model.addAttribute("contactForm", new ContactForm());
        return "/user/add_contact";
    }

    @PostMapping("/add")
    public String saveContact(@Valid @ModelAttribute ContactForm contactForm, BindingResult bindingResult, Authentication authentication, HttpSession session) throws IOException {

        // Fetched the form data - ContactForm
        // System.out.println(contactForm);

        // Validate the data
        if (bindingResult.hasErrors()) {
            session.setAttribute("message", new Message("Please correct the following error", MessageType.red));
            return "/user/add_contact";
        }
        Contact contactToSave = generateContactToSave(new Contact(), contactForm, authentication);
        contactService.saveContact(contactToSave);
        session.setAttribute("message", new Message("Contact Saved Successfully", MessageType.green));
        return "redirect:/user/contacts/add";
    }

    @GetMapping
    public String viewContacts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
                               @RequestParam(defaultValue = "name") String sortField,
                               @RequestParam(defaultValue = "asc") String direction, Model model,
                               Authentication authentication) {
        String userName = EmailHelper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(userName);
        Optional<Page<Contact>> contactPageOptional = contactService.getContactByUser(user, page, size, sortField, direction);
        contactPageOptional.ifPresent(contactPage -> {
            model.addAttribute("contacts", contactPage);
            model.addAttribute("pageSize", PAGE_SIZE);
            model.addAttribute("contactSearchForm", new ContactSearchForm());
        });
        return "user/all_contacts";
    }

    // Search Handler
    @GetMapping("/search")
    public String searchHandler(@ModelAttribute ContactSearchForm contactSearchForm,
                                @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
                                @RequestParam(defaultValue = "name") String sortField,
                                @RequestParam(defaultValue = "asc") String direction, Model model,
                                Authentication authentication) {
        String field = contactSearchForm.getField();
        String keyword = contactSearchForm.getKeyword();
        String userName = EmailHelper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(userName);

        Optional<Page<Contact>> contactPageOptional = Optional.empty();
        contactPageOptional = switch (field.toLowerCase()) {
            case "name" -> contactService.searchByName(user, keyword, page, size, sortField, direction);
            case "email" -> contactService.searchByEmail(user, keyword, page, size, sortField, direction);
            case "phone" -> contactService.searchByPhoneNumber(user, keyword, page, size, sortField, direction);
            default -> contactPageOptional;
        };
        contactPageOptional.ifPresent(contactPage -> {
            model.addAttribute("contacts", contactPage);
            model.addAttribute("pageSize", PAGE_SIZE);
            model.addAttribute("contactSearchForm", contactSearchForm);
        });
        return "user/search";
    }

    @RequestMapping("/delete/{id}")
    public String deleteContact(@PathVariable String id, HttpSession session) {
        contactService.deleteContact(id);
        session.setAttribute("message", new Message("Contact Deleted Successfully", MessageType.red));
        return "redirect:/user/contacts";
    }

    @GetMapping("/view/{id}")
    public String loadContactForm(@PathVariable String id, Model model) {
        contactService.getContactById(id).ifPresent(contact -> {
            ContactForm contactForm = new ContactForm();
            populateContactForm(contact, contactForm);
            model.addAttribute("contactForm", contactForm);
            model.addAttribute("contactId", id);
        });
        return "user/update_contact_view";
    }

    @PostMapping("/update/{id}")
    public String updateContact(@PathVariable String id, @Valid @ModelAttribute ContactForm contactForm,
                                BindingResult bindingResult, Authentication authentication, HttpSession session,
                                Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            session.setAttribute("message", new Message("Please correct the following error", MessageType.red));
            model.addAttribute("contactId", id);
            return "/user/update_contact_view";
        }
        Optional<Contact> contactFoundById = contactService.getContactById(id);
        if (contactFoundById.isPresent()) {
            Contact contactToSave = generateContactToSave(contactFoundById.get(), contactForm, authentication);
            contactToSave.setId(id);
            contactService.updateContact(contactToSave);
            session.setAttribute("message", new Message("Contact Updated Successfully", MessageType.green));
        } else {
            session.setAttribute("message", new Message("Contact not found", MessageType.red));
        }
        return "redirect:/user/contacts";
    }

    private void populateContactForm(Contact contact, ContactForm contactForm) {
        contactForm.setName(contact.getName());
        contactForm.setEmail(contact.getEmail());
        contactForm.setPhoneNumber(contact.getPhoneNumber());
        contactForm.setAddress(contact.getAddress());
        contactForm.setDescription(contact.getDescription());
        contactForm.setPicture(contact.getPicture());
        contactForm.setFavourite(contact.isFavourite());
        contactForm.setWebsiteLink(contact.getWebsiteLink());
        contactForm.setLinkedInLink(contact.getLinkedInLink());
    }

    private Contact generateContactToSave(Contact contact, ContactForm contactForm, Authentication authentication) throws IOException {
        String userEmail = EmailHelper.getEmailOfLoggedInUser(authentication);
        User contactOwner = userService.getUserByEmail(userEmail);
        String fileName = null;
        String fileURL = null;

        if (!contactForm.getContactImage().isEmpty()) {
            fileName = UUID.randomUUID().toString();
            fileURL = imageService.uploadImage(contactForm.getContactImage(), fileName);
        } else if (contact.getPicture() != null) {
            fileName = contact.getCloudinaryImagePublicId();
            fileURL = contact.getPicture();
        }

        return Contact.builder()
                .name(contactForm.getName())
                .email(contactForm.getEmail())
                .phoneNumber(contactForm.getPhoneNumber())
                .address(contactForm.getAddress())
                .description(contactForm.getDescription())
                .websiteLink(contactForm.getWebsiteLink())
                .linkedInLink(contactForm.getLinkedInLink())
                .favourite(contactForm.isFavourite())
                .picture(fileURL)
                .cloudinaryImagePublicId(fileName)
                .user(contactOwner)
                .build();
    }
}