package com.cdm.controllers;

import com.cdm.entities.User;
import com.cdm.forms.UserForm;
import com.cdm.helpers.Message;
import com.cdm.helpers.MessageType;
import com.cdm.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;

@Controller
public class PageController {

    @Autowired
    private UserService userService;

    @Value("${defaultPicture.url}")
    private String defaultPicURL;

    @GetMapping("/")
    public String index() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(Model model) {
        System.out.println("Home Page Handlers");
        /*
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put("name", "Contact Directory Manager");
        attributes.put("githubUrl", "https://github.com/VivekChaurasia03?tab=repositories");
        attributes.put("message", "This is a contact directory manager");
        model.addAllAttributes(attributes);
        */
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        System.out.println("Login Page Handlers");
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        // Sending blank userForm
        UserForm userForm = new UserForm();
        // userForm.setAbout("This is coming from backend.....");
        model.addAttribute("userForm", userForm);
        return "register";
    }

    // Processing Register
    @PostMapping(value = "/do-register")
    public String processRegister(@Valid @ModelAttribute UserForm userForm, BindingResult bindingResult, HttpSession session) {
        // Fetched the form data - UserForm

        // TODO: Validate the data
        if(bindingResult.hasErrors()) {
            return "register";
        }

        // Extracted the data from userForm
        /*
        User user = User.builder()
                .name(userForm.getName())
                .email(userForm.getEmail())
                .password(userForm.getPassword())
                .about(userForm.getAbout())
                .phoneNumber(userForm.getPhoneNumber())
                .profilePic(defaultPicURL)
                .build();
         */
        User userToSave = getUserToSave(userForm);

        // Save to dB
        User savedUser = userService.saveUser(userToSave, session);

        if (savedUser == null) {
            return "register";
        }

        // message = "Registration Successful"
        Message message = Message.builder().content("Registration Successful").type(MessageType.green).build();
        session.setAttribute("message", message);

        // Redirect to login page
        return "redirect:/register";
    }

    private User getUserToSave(UserForm userForm) {
        User userToSave = new User();
        userToSave.setName(userForm.getName());
        userToSave.setEmail(userForm.getEmail());
        userToSave.setPassword(userForm.getPassword());
        userToSave.setAbout(userForm.getAbout());
        userToSave.setPhoneNumber(userForm.getPhoneNumber());
        userToSave.setProfilePic(defaultPicURL);
        return userToSave;
    }
}
