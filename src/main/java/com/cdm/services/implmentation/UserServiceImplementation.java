package com.cdm.services.implmentation;

import com.cdm.entities.User;
import com.cdm.helpers.Message;
import com.cdm.helpers.MessageType;
import com.cdm.helpers.AppConstants;
import com.cdm.helpers.ResourceNotFoundException;
import com.cdm.repositories.UserRepository;
import com.cdm.services.EmailService;
import com.cdm.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.cdm.helpers.EmailHelper.getLinkForEmailVerification;

@Service
public class UserServiceImplementation implements UserService {

    private Logger log = LoggerFactory.getLogger(UserServiceImplementation.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Override
    public User saveUser(User user, HttpSession session) {
        // Need to generate the userId and email verification token
        String userId = UUID.randomUUID().toString();
        String emailToken = UUID.randomUUID().toString();
        user.setUserId(userId);
        user.setEmailToken(emailToken);

        // Password Encoding
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Setting the Default Role
        user.setRoleList(List.of(AppConstants.ROLE_USER));

        // Sending Link to User for verification
        String link = getLinkForEmailVerification(emailToken);

        try {
            // Try to send the email
            emailService.sendEmail(user.getEmail(), "Verify Account: Contact Directory Manager", link);
        } catch (Exception e) {
            // A fallback message in the session
            Message fallbackMessage = Message.builder()
                    .content("This is a development environment. Only the email greycr80@gmail.com can be used for login/testing. Also you can login through services like google/github.")
                    .type(MessageType.yellow)
                    .build();
            session.setAttribute("message", fallbackMessage);
            return null;
        }

        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(String userId) {
        return userRepository.findById(userId);
    }

    @Override
    public Optional<User> updateUser(User user) {
        User foundedUser = userRepository.findById(user.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
        foundedUser.setName(user.getName());
        foundedUser.setEmail(user.getEmail());
        foundedUser.setPassword(user.getPassword());
        foundedUser.setPhoneNumber(user.getPhoneNumber());
        foundedUser.setAbout(user.getAbout());
        foundedUser.setProfilePic(user.getProfilePic());
        foundedUser.setEnabled(user.isEnabled());
        foundedUser.setEmailVerified(user.isEmailVerified());
        foundedUser.setPhoneVerified(user.isPhoneVerified());
        foundedUser.setProvider(user.getProvider());
        foundedUser.setProviderUserId(user.getProviderUserId());

        // Save the user
        return Optional.of(userRepository.save(foundedUser));
    }

    @Override
    public void deleteUser(String userId) {
        User foundedUser = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
        userRepository.delete(foundedUser);
    }

    @Override
    public boolean isUserExist(String userId) {
        User foundedUser = userRepository.findById(userId).orElse(null);
        return foundedUser != null;
    }

    @Override
    public boolean isUserExistByEmail(String email) {
        User foundedUser = userRepository.findByEmail(email).orElse(null);
        return foundedUser != null;
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public User getUserByToken(String token) {
        return userRepository.findByEmailToken(token).orElse(null);
    }
}
