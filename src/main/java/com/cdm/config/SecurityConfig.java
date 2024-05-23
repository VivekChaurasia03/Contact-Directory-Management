package com.cdm.config;

import com.cdm.services.implmentation.SecurityUserServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    private final SecurityUserServiceImplementation userDetailsService;

    private final OAuthAuthenticationSuccessHandler oAuthAuthenticationSuccessHandler;

    @Autowired
    public SecurityConfig(SecurityUserServiceImplementation userDetailsService, OAuthAuthenticationSuccessHandler oAuthAuthenticationSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.oAuthAuthenticationSuccessHandler = oAuthAuthenticationSuccessHandler;
    }

    /*
    // User login by creating an in memory user and using the service - UserDetailService used for in-memory
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails userDetailsAdmin = User.withDefaultPasswordEncoder().username("admin123").password("admin123").roles("ADMIN", "USER").build();
        UserDetails userDetailsUser = User.withDefaultPasswordEncoder().username("user123").password("user123").roles("USER").build();
        // Can pass single or multiple userDetails
        return new InMemoryUserDetailsManager(userDetailsAdmin, userDetailsUser);
    }
    */

    // User login using Database
    /*
     * AuthenticationProvider is responsible for authenticating a user's credentials and returning an Authentication
     * object that represents the authenticated user
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

        // Object of userDetailService
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);

        // Object of PasswordEncoder
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        // daoAuthenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder());

        return daoAuthenticationProvider;
    }

    // Securing specific URLs
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        // Config
        httpSecurity.authorizeHttpRequests(authorize -> {
            // authorize.requestMatchers("/home", "/register", "/services", "/about", "/contact").permitAll();
            authorize.requestMatchers("/user/**").authenticated();
            authorize.anyRequest().permitAll();
        });

        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        System.out.println("The point is coming here");
        // To make the basic Spring provided form login withDefaults
        httpSecurity.formLogin(formLogin -> {
            formLogin.loginPage("/login");
            formLogin.loginProcessingUrl("/authenticate");
            formLogin.defaultSuccessUrl("/user/dashboard", true);
            formLogin.failureUrl("/login?error=true");
            formLogin.usernameParameter("email");
            formLogin.passwordParameter("password");
        });

        httpSecurity.logout(logoutForm -> {
            logoutForm.logoutUrl("/logout");
            logoutForm.logoutSuccessUrl("/login?logout=true");
        });

        // OAuth Configs
        httpSecurity.oauth2Login(oauth -> {
            oauth.loginPage("/login");
            oauth.successHandler(oAuthAuthenticationSuccessHandler);
        });

        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}