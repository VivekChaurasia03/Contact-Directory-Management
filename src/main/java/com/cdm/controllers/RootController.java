package com.cdm.controllers;

import com.cdm.entities.User;
import com.cdm.helpers.EmailHelper;
import com.cdm.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.beans.factory.annotation.Autowired;

@ControllerAdvice
public class RootController {

    private static final Logger logger = LoggerFactory.getLogger(RootController.class);

    private final UserService userService;

    @Autowired
    public RootController(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute
    public void addLoggedInUserInformation(Model model, Authentication authentication) {
        if(authentication == null) return;
        logger.info("Triggered for all Reqs");
        String userName = EmailHelper.getEmailOfLoggedInUser(authentication);
        User loggedInUser = userService.getUserByEmail(userName);

        if (loggedInUser != null) {
            logger.info("The name of the user is {} and email is {}", loggedInUser.getName(), loggedInUser.getEmail());
        } else {
            logger.info("No user found with email {}", userName);
        }

        model.addAttribute("loggedInUser", loggedInUser);
    }
}