package com.cdm.controllers;

import com.cdm.entities.User;
import com.cdm.helpers.EmailHelper;
import com.cdm.services.UserService;
import lombok.experimental.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    private final static Logger logger = LoggerFactory.getLogger(UserController.class);

    // User dashboard
    @GetMapping("/dashboard")
    public String userDashboard() {
        System.out.println("userDashboard Triggered!");
        return "user/dashboard";
    }

    // User profile page
    @GetMapping("/profile")
    public String userProfile() {
        System.out.println("userProfile Triggered!");
        return "user/profile";
    }

    // User add contacts page

    // User view contacts

    // User edit contact

    // User delete contact

    // User search contact

}
