package com.cdm.config;

import com.cdm.entities.Providers;
import com.cdm.entities.User;
import com.cdm.helpers.AppConstants;
import com.cdm.repositories.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class OAuthAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuthAuthenticationSuccessHandler.class);
    private final UserRepository userRepository;

    @Autowired
    public OAuthAuthenticationSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        logger.info("OAuthAuthenticationSuccessHandler");

        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        String authorizedClientRegistrationId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();
        logger.info("Authorized Client Registration ID: {}", authorizedClientRegistrationId);

        DefaultOAuth2User authenticatedUser = getAuthenticatedUser(authentication);
        String provider = authorizedClientRegistrationId.toUpperCase();

        if (provider.equals("GOOGLE") || provider.equals("GITHUB")) {
            validateAndSaveUser(authenticatedUser, Providers.valueOf(provider));
        }

        new DefaultRedirectStrategy().sendRedirect(request, response, "/user/profile");
    }

    private void validateAndSaveUser(DefaultOAuth2User authenticatedUser, Providers provider) {
        User userToBeSaved = createUser(authenticatedUser, provider);
        userRepository.findByEmail(userToBeSaved.getEmail()).ifPresentOrElse(
                existingUser -> logger.info("User already exists: {}", existingUser),
                () -> {
                    userRepository.save(userToBeSaved);
                    logger.info("User saved to the DB: {}", userToBeSaved.getEmail());
                }
        );
    }

    private DefaultOAuth2User getAuthenticatedUser(Authentication authentication) {
        DefaultOAuth2User authenticatedUser = (DefaultOAuth2User) authentication.getPrincipal();
        logger.info("Authenticated User Name: {}", authenticatedUser.getName());
        authenticatedUser.getAttributes().forEach((key, value) -> logger.info("{} : {}", key, value));
        logger.info("User Authorities: {}", authenticatedUser.getAuthorities());
        return authenticatedUser;
    }

    private User createUser(DefaultOAuth2User authenticatedUser, Providers provider) {
        String userEmail = Objects.requireNonNullElse(
                authenticatedUser.getAttribute("email"),
                authenticatedUser.getAttribute("login") + "@gmail.com"
        );
        String userName = Objects.requireNonNull(authenticatedUser.getAttribute("name")).toString();
        String userProfilePicture = Objects.requireNonNull(authenticatedUser.getAttribute("avatar_url") != null ?
                authenticatedUser.getAttribute("avatar_url") : authenticatedUser.getAttribute("picture")).toString();

        User user = new User();
        user.setUserId(UUID.randomUUID().toString());
        user.setEmail(userEmail);
        user.setName(userName);
        user.setProfilePic(userProfilePicture);
        user.setPassword("password"); // This should be externalized and encrypted
        user.setEnabled(true);
        user.setEmailVerified(true);
        user.setProvider(provider);
        user.setProviderUserId(authenticatedUser.getName());
        user.setRoleList(List.of(AppConstants.ROLE_USER));
        user.setAbout(provider == Providers.GITHUB ? authenticatedUser.getAttribute("bio") : "This account is created using Google...");

        return user;
    }
}