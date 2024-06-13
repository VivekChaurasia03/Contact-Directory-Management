package com.cdm.controllers;

import com.cdm.entities.Contact;
import com.cdm.entities.User;
import com.cdm.helpers.EmailHelper;
import com.cdm.services.ContactService;
import com.cdm.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@ControllerAdvice
public class RootController {

    private static final Logger logger = LoggerFactory.getLogger(RootController.class);

    private final UserService userService;

    private final ContactService contactService;

    @Autowired
    public RootController(UserService userService, ContactService contactService) {
        this.userService = userService;
        this.contactService = contactService;
    }

    @ModelAttribute
    public void addLoggedInUserInformation(Model model, Authentication authentication) {
        if (authentication == null) return;
        // logger.info("Triggered for all Reqs");
        String userName = EmailHelper.getEmailOfLoggedInUser(authentication);
        User loggedInUser = userService.getUserByEmail(userName);

        /*
        if (loggedInUser != null) {
            logger.info("The name of the user is {} and email is {}", loggedInUser.getName(), loggedInUser.getEmail());
        } else {
            logger.info("No user found with email {}", userName);
        }
         */

        Optional<List<Contact>> contactListOptional = contactService.getContactByUser(loggedInUser);
        if (contactListOptional.isPresent()) {
            List<Contact> contactList = contactListOptional.get();
            model.addAttribute("contactListSize", contactList.size());
        } else {
            model.addAttribute("contactListSize", 0);
        }

        model.addAttribute("loggedInUser", loggedInUser);
    }
}