package com.cdm.controllers;

import com.cdm.entities.User;
import com.cdm.helpers.Message;
import com.cdm.helpers.MessageType;
import com.cdm.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/auth")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final UserService userService;

    @Autowired
    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    // Verify email
    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam("token") String token, HttpSession httpSession) {
        logger.info("Verifying email for token: {}", token);
        User user = userService.getUserByToken(token);
        if (user == null) {
            logger.error("Invalid token: {}", token);
            httpSession.setAttribute("message", Message.builder()
                    .type(MessageType.red)
                    .content("Invalid token! Please check the link.")
                    .build());
            return "error_page";
        }
        if (!user.getEmailToken().equals(token)) {
            logger.error("Token mismatch for user: {}", user.getEmail());
            httpSession.setAttribute("message", Message.builder()
                    .type(MessageType.red)
                    .content("Token mismatch! Please check the link.")
                    .build());
            return "error_page";
        }

        if (user.isEmailVerified()) {
            logger.error("Token already used for user: {}", user.getEmail());
            httpSession.setAttribute("message", Message.builder()
                    .type(MessageType.red)
                    .content("This token has already been used.")
                    .build());
            return "error_page";
        }

        user.setEmailVerified(true);
        user.setEnabled(true);
        user.setEmailToken(null);
        userService.updateUser(user);
        logger.info("Email verified successfully for user: {}", user.getEmail());

        httpSession.setAttribute("message", Message.builder()
                .type(MessageType.green)
                .content("Email verified successfully! You can now log in.")
                .build());

        return "login";
    }
}
