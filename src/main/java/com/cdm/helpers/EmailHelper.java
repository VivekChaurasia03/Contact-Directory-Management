package com.cdm.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Objects;

public class EmailHelper {

    private static final Logger logger = LoggerFactory.getLogger(EmailHelper.class);

    public static String getEmailOfLoggedInUser(Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken oAuth2Token) {
            return getEmailFromOAuth2Token(oAuth2Token);
        } else {
            return getEmailFromStandardAuth(authentication);
        }
    }

    private static String getEmailFromOAuth2Token(OAuth2AuthenticationToken oAuth2Token) {
        String provider = oAuth2Token.getAuthorizedClientRegistrationId().toUpperCase();
        // logger.info("Authorized Client Registration ID: {}", provider);

        DefaultOAuth2User authenticatedUser = getAuthenticatedUser(oAuth2Token);

        return switch (provider) {
            case AppConstants.GOOGLE -> {
                // logger.info("Authenticated via Google");
                yield Objects.requireNonNull(authenticatedUser.getAttribute("email"), "Google email is null");
            }
            case AppConstants.GITHUB -> {
                // logger.info("Authenticated via GitHub");
                yield Objects.requireNonNullElse(
                        authenticatedUser.getAttribute("email"),
                        authenticatedUser.getAttribute("login") + "@gmail.com"
                );
            }
            default -> {
                logger.warn("Unsupported OAuth provider: {}", provider);
                yield null;
            }
        };
    }

    private static String getEmailFromStandardAuth(Authentication authentication) {
        return authentication.getName();
    }

    private static DefaultOAuth2User getAuthenticatedUser(OAuth2AuthenticationToken oAuth2Token) {
        return (DefaultOAuth2User) oAuth2Token.getPrincipal();
    }
}