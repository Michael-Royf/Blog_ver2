package com.michael.blog.service.impl;

import com.michael.blog.constants.UserConstant;
import com.michael.blog.entity.ConfirmationToken;
import com.michael.blog.entity.ImageData;
import com.michael.blog.entity.Token;
import com.michael.blog.entity.User;
import com.michael.blog.entity.enumeration.TokenType;
import com.michael.blog.entity.enumeration.UserRole;
import com.michael.blog.exception.payload.EmailExistException;
import com.michael.blog.exception.payload.ImageNotFoundException;
import com.michael.blog.exception.payload.UserNotFoundException;
import com.michael.blog.exception.payload.UsernameExistException;
import com.michael.blog.payload.request.LoginRequest;
import com.michael.blog.payload.request.UserRequest;
import com.michael.blog.payload.response.JwtAuthResponse;
import com.michael.blog.payload.response.MessageResponse;
import com.michael.blog.payload.response.UserResponse;
import com.michael.blog.repository.ConfirmationTokenRepository;
import com.michael.blog.repository.ImageDataRepository;
import com.michael.blog.repository.TokenRepository;
import com.michael.blog.repository.UserRepository;
import com.michael.blog.security.JwtTokenProvider;
import com.michael.blog.service.ConfirmationTokenService;
import com.michael.blog.service.EmailSender;
import com.michael.blog.service.UserService;
import com.michael.blog.utility.EmailBuilder;
import com.michael.blog.utility.ImageUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

import static com.michael.blog.constants.FileConstant.NOT_AN_IMAGE_FILE;
import static com.michael.blog.constants.FileConstant.TEMP_PROFILE_IMAGE_BASE_URL;
import static com.michael.blog.constants.SecurityConstant.*;
import static com.michael.blog.constants.UserConstant.*;
import static org.springframework.http.MediaType.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    public static final String LINK_FOR_CONFIRMATION = "http://localhost:8080/api/v1/registration/confirm?token=";

    private final ModelMapper mapper;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final ConfirmationTokenService tokenService;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final EmailBuilder emailBuilder;
    private final EmailSender emailSender;
    private final ImageDataRepository imageDataRepository;
    private final ImageUtils imageUtils;

    public UserServiceImpl(ModelMapper mapper,
                           AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           TokenRepository tokenRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider,
                           ConfirmationTokenService tokenService,
                           ConfirmationTokenRepository confirmationTokenRepository,
                           EmailBuilder emailBuilder,
                           EmailSender emailSender,
                           ImageDataRepository imageDataRepository,
                           ImageUtils imageUtils) {
        this.mapper = mapper;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenService = tokenService;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.emailBuilder = emailBuilder;
        this.emailSender = emailSender;
        this.imageDataRepository = imageDataRepository;
        this.imageUtils = imageUtils;
    }

    @Override
    public String register(UserRequest registerRequest) throws IOException {
        validateNewUsernameAndEmail(StringUtils.EMPTY, registerRequest.getUsername(), registerRequest.getEmail());
        String password = generatePassword();
        User user = User.builder()
                //   .generateId(generateUserID())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(password))
                .role(UserRole.ROLE_ADMIN)
                .lastLoginDate(new Date())
                .isNotLocked(true)
                .build();

        saveProfileTempImage(user);
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
    public JwtAuthResponse login(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String username = authenticate.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username));
        String jwtAccessToken = jwtTokenProvider.generateAccessToken(username);
        String jwtRefreshToken = jwtTokenProvider.generateRefreshToken(username);

        Token token = createTokenForDB(user, jwtAccessToken);
        revokeAllUserTokens(user);
        tokenRepository.save(token);
        return JwtAuthResponse.builder()
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken)
                .build();
    }

    @Override
    public JwtAuthResponse refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getTokenFromRequest(request);

        if (org.springframework.util.StringUtils.hasText(refreshToken)
                && jwtTokenProvider.validateToken(refreshToken)) {
            String username = jwtTokenProvider.getUsername(refreshToken);
            User user = findUserByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username));

            String accessToken = jwtTokenProvider.generateAccessToken(username);
            revokeAllUserTokens(user);
            Token token = createTokenForDB(user, accessToken);
            tokenRepository.save(token);
            return JwtAuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        }
        throw new RuntimeException("Refresh token not found or expired");
    }


    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (org.springframework.util.StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
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

    @Override
    public UserResponse updateProfileImage(MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException {
        User user = getLoggedInUser();
        user = saveProfileImage(user, profileImage);
        return mapper.map(user, UserResponse.class);
    }

    @Override
    public MessageResponse deleteProfileImage() throws IOException {
        User user = getLoggedInUser();
        ImageData imageData = imageDataRepository.findByFileName(user.getProfileImageFileName())
                .orElseThrow(() -> new ImageNotFoundException("Image not found"));
        imageDataRepository.delete(imageData);

        saveProfileTempImage(user);
        userRepository.save(user);
        return new MessageResponse(String.format("Image with file name %s was deleted", imageData.getFileName()));
    }

    @Override
    public byte[] getProfileImage() {
        User user = getLoggedInUser();
        ImageData imageData = imageDataRepository
                .findByFileName(user.getProfileImageFileName()).orElseThrow(() -> new ImageNotFoundException("Not found image"));
        return imageUtils.decompressImage(imageData.getData());
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

    private String generateUserID() {
        return RandomStringUtils.randomNumeric(10);
    }

    private void revokeAllUserTokens(User user) {
        List<Token> validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if (validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }


    private Token createTokenForDB(User user, String accessToken) {
        return Token.builder()
                .user(user)
                .token(accessToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
    }

    private User saveProfileImage(User user, MultipartFile profileImage) throws IOException {
        if (profileImage != null) {
            if (!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(profileImage.getContentType())) {
                throw new RuntimeException(profileImage.getOriginalFilename() + NOT_AN_IMAGE_FILE);
            }

            if (user.getProfileImageFileName() != null) {
                Optional<ImageData> imageDataDb = imageDataRepository
                        .findByFileName(user.getProfileImageFileName());
                imageDataRepository.delete(imageDataDb.get());
            }

            ImageData imageData = ImageData.builder()
                    .fileName(profileImage.getOriginalFilename())
                    .fileType(profileImage.getContentType())
                    .data(imageUtils.compressImage(profileImage.getBytes()))
                    .build();
            imageData = imageDataRepository.save(imageData);
            user.setProfileImageFileName(imageData.getFileName());
            log.info("Saved file in database by name: " + profileImage.getOriginalFilename());
            return userRepository.save(user);
        }
        throw new RuntimeException("Image not found");
    }


    private void saveProfileTempImage(User user) throws IOException {
        ImageData imageData = ImageData.builder()
                .fileName(user.getUsername() + "_temporaryImage")
                .fileType(IMAGE_JPEG_VALUE)
                .data(imageUtils.compressImage(getTempImage(user.getUsername())))
                .build();
        imageData = imageDataRepository.save(imageData);
        user.setProfileImageFileName(imageData.getFileName());
        log.info("Saved file in database by name: " + imageData.getFileName());
    }


    private byte[] getTempImage(String username) throws IOException {
        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (InputStream inputStream = url.openStream()) {
            int bytesRead;
            byte[] chunk = new byte[1024];
            while ((bytesRead = inputStream.read(chunk)) > 0) {
                byteArrayOutputStream.write(chunk, 0, bytesRead);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

}
