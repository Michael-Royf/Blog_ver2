package com.michael.blog.config;

import com.michael.blog.entity.User;
import com.michael.blog.repository.UserRepository;
import com.michael.blog.service.impl.LoginAttemptService;
import com.michael.blog.utility.ImageUtils;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua_parser.Parser;

import java.io.IOException;
import java.util.Date;

@Configuration
@Slf4j
public class ApplicationConfig {

    private final UserRepository userRepository;
    private final LoginAttemptService loginAttemptService;
    private final ImageUtils imageUtils;

    public ApplicationConfig(UserRepository userRepository,
                             LoginAttemptService loginAttemptService,
                             ImageUtils imageUtils) {
        this.userRepository = userRepository;
        this.loginAttemptService = loginAttemptService;
        this.imageUtils = imageUtils;
    }

    @Bean
    public Parser parser(){
        return new Parser();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return usernameOrEmail  -> {
            User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                    .orElseThrow(() -> new UsernameNotFoundException(String.format("User with username %s not found", usernameOrEmail)));
            validateLoginAttempt(user);
            user.setDisplayLastLoginDate(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            imageUtils.copyImageToLocalSystemFromDB(user);
            return user;
        };
    }

    private void validateLoginAttempt(User user) {
        if (user.getIsNotLocked()) {
            if (loginAttemptService.hasExceededMaxAttempts(user.getUsername())) {
                user.setIsNotLocked(false);
            } else {
                user.setIsNotLocked(true);
            }
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }

    }
}
