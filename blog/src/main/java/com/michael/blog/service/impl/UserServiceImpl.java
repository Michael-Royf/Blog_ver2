package com.michael.blog.service.impl;

import com.michael.blog.constants.UserConstant;
import com.michael.blog.entity.ConfirmationToken;
import com.michael.blog.entity.User;
import com.michael.blog.entity.enumeration.UserRole;
import com.michael.blog.exception.payload.EmailExistException;
import com.michael.blog.exception.payload.UserNotFoundException;
import com.michael.blog.exception.payload.UsernameExistException;
import com.michael.blog.payload.request.LoginRequest;
import com.michael.blog.payload.request.UserRequest;
import com.michael.blog.payload.response.UserResponse;
import com.michael.blog.repository.ConfirmationTokenRepository;
import com.michael.blog.repository.UserRepository;
import com.michael.blog.security.JwtService;
import com.michael.blog.service.ConfirmationTokenService;
import com.michael.blog.service.EmailSender;
import com.michael.blog.service.UserService;
import com.michael.blog.utility.EmailBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static com.michael.blog.constants.SecurityConstant.VERIFICATION_TOKEN_EXPIRED;
import static com.michael.blog.constants.SecurityConstant.VERIFICATION_TOKEN_NOT_FOUND;
import static com.michael.blog.constants.UserConstant.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    public static final String LINK_FOR_CONFIRMATION = "http://localhost:8080/api/v1/registration/confirm?token=";

    private ModelMapper mapper;
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    //private JwtTokenProvider jwtTokenProvider;
    private JwtService jwtService;
    private ConfirmationTokenService tokenService;
    private ConfirmationTokenRepository confirmationTokenRepository;
    private EmailBuilder emailBuilder;
    private EmailSender emailSender;

    @Autowired
    public UserServiceImpl(ModelMapper mapper, AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, ConfirmationTokenService tokenService, ConfirmationTokenRepository confirmationTokenRepository, EmailBuilder emailBuilder, EmailSender emailSender) {
        this.mapper = mapper;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenService = tokenService;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.emailBuilder = emailBuilder;
        this.emailSender = emailSender;
    }


    @Override
    public String login(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String username = authenticate.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException("User not found"));
        return jwtService.generateToken(user);
    }

    @Override
    public String register(UserRequest registerRequest) {

        validateNewUsernameAndEmail(StringUtils.EMPTY, registerRequest.getUsername(), registerRequest.getEmail());

        String password = generatePassword();

        User user = User.builder()
                .userId(generatePassword())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(password))
                .role(UserRole.ROLE_SUPERADMIN)
                .lastLoginDate(new Date())
                .isNotLocked(true)
                .build();
        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken =
                ConfirmationToken.builder()
                        .token(token)
                        .createdAt(LocalDateTime.now())
                        .expiredAt(LocalDateTime.now().plusMinutes(15))
                        .user(user)
                        .build();
        tokenService.saveConfirmationToken(confirmationToken);

        String link = LINK_FOR_CONFIRMATION + token;
        emailSender.sendEmailForVerification(
                user.getEmail(),
                emailBuilder.buildEmailForConfirmationEmail(user.getFirstName(), link));
        emailSender.sendNewPassword(user.getEmail(), user.getFirstName(), password);
        return "User registered successfully!";
    }

    @Override
    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException(VERIFICATION_TOKEN_NOT_FOUND));
        if (confirmationToken.getConfirmedAt() != null) {
            throw new RuntimeException(EMAIL_ALREADY_CONFIRMED);
        }
        LocalDateTime expiredDate = confirmationToken.getExpiredAt();
        if (expiredDate.isBefore(LocalDateTime.now())) {
            throw new RuntimeException(VERIFICATION_TOKEN_EXPIRED);
        }
        confirmationTokenRepository.updateConfirmedDate(token, LocalDateTime.now());
        userRepository.enableUser(confirmationToken.getUser().getEmail());
        return CONFIRMED;
    }


    @Override
    public UserResponse getUserById(Long id) {
        User user = getUserFromDbById(id);
        return mapper.map(user, UserResponse.class);
    }

    @Override
    public UserResponse getMyProfile() {
      User user = getLoggedInUser();
      return mapper.map(user, UserResponse.class);
    }

    @Override
    public UserResponse updateUser(UserRequest registerRequest) {
        return null;
    }




    @Override
    public String deleteUser() {
        User user = getLoggedInUser();
        userRepository.delete(user);
        return String.format(UserConstant.USER_DELETED, user.getUsername());
    }

    @Override
    public String deactivateProfile() {
        User user = getLoggedInUser();
        userRepository.disabledUser(user.getEmail());
        return "Your profile is disabled.\n" +
                "To activate your profile, contact support.";
    }

    @Override
    public User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username));
    }

    @Override
    public String changePassword(String oldPassword, String newPassword) {
        User user = getLoggedInUser();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("The old password was entered incorrectly");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return "Password was updated";
    }


    @Override
    public String forgotPassword(String email) {
        User user = findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException(NO_USER_FOUND_BY_EMAIL + email));
        String newPassword = generatePassword();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        emailSender.sendNewPassword(email, user.getFirstName(), newPassword);
        return NEW_PASSWORD_SEND_EMAIL;
    }


    private User getUserFromDbById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format(UserConstant.NO_USER_FOUND_BY_ID, userId)));
    }


    private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws UserNotFoundException, UsernameExistException, EmailExistException {
        User userByNewUsername = findUserByUsername(newUsername).orElse(null);
        User userByNewEmail = findUserByEmail(newEmail).orElse(null);
        if (StringUtils.isNotBlank(currentUsername)) {
            User currentUser = findUserByUsername(currentUsername).orElse(null);
            if (currentUser == null) {
                throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + currentUsername);
            }
            if (userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;
        } else {
            if (userByNewUsername != null) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail != null) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return null;
        }
    }

    private Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    private Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

}
