package com.michael.blog.service.PostServiceImpl;

import com.michael.blog.constants.UserConstant;
import com.michael.blog.entity.Role;
import com.michael.blog.entity.User;
import com.michael.blog.exception.payload.EmailExistException;
import com.michael.blog.exception.payload.UserNotFoundException;
import com.michael.blog.exception.payload.UsernameExistException;
import com.michael.blog.payload.request.LoginRequest;
import com.michael.blog.payload.request.UserRequest;
import com.michael.blog.payload.response.UserResponse;
import com.michael.blog.repository.RoleRepository;
import com.michael.blog.repository.UserRepository;
import com.michael.blog.security.JwtTokenProvider;
import com.michael.blog.service.UserService;
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

import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.michael.blog.constants.UserConstant.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private ModelMapper mapper;
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private RoleRepository roleRepository;
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserServiceImpl(ModelMapper mapper,
                           AuthenticationManager authenticationManager,
                           UserRepository userRepository, PasswordEncoder passwordEncoder,
                           RoleRepository roleRepository,
                           JwtTokenProvider jwtTokenProvider) {
        this.mapper = mapper;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @Override
    public String login(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        return jwtTokenProvider.generateToken(authenticate);
    }

    @Override
    public String register(UserRequest registerRequest) {

        validateNewUsernameAndEmail(StringUtils.EMPTY, registerRequest.getUsername(), registerRequest.getEmail());

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("ROLE_USER").get();
        roles.add(userRole);
        User user = User.builder()
                .userId(generatePassword())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .roles(roles)
                .lastLoginDate(new Date())
                .build();

        userRepository.save(user);
        return "user registered successfully!";
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = getUserFromDbById(id);
        return mapper.map(user, UserResponse.class);
    }


    @Override
    public UserResponse updateUser(Long id, UserRequest registerRequest) {
//        User user = getUserFromDbById(id);
//        user.setName(registerRequest.getName());
//        user.
        return null;
    }

    @Override
    public String deleteUser(Long id) {
        User user = getUserFromDbById(id);
        userRepository.delete(user);
        return String.format(UserConstant.USER_DELETED, user.getUsername());
    }

    @Override
    public User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username));
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
