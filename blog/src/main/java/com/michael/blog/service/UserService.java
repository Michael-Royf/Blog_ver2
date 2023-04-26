package com.michael.blog.service;

import com.michael.blog.entity.User;
import com.michael.blog.payload.request.LoginRequest;
import com.michael.blog.payload.request.PasswordChangeRequest;
import com.michael.blog.payload.request.UserRequest;
import com.michael.blog.payload.response.JwtAuthResponse;
import com.michael.blog.payload.response.MessageResponse;
import com.michael.blog.payload.response.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {

    String register(UserRequest registerRequest) throws IOException;

    JwtAuthResponse login(LoginRequest loginRequest);

    JwtAuthResponse refreshToken(HttpServletRequest request, HttpServletResponse response);

    UserResponse getUserById(Long id);

    UserResponse getMyProfile();

    UserResponse updateUser(UserRequest registerRequest);

    String deleteUser();

    String deactivateProfile();

    User getLoggedInUser();

    String changePassword(PasswordChangeRequest passwordChangeRequest);

    String confirmToken(String token);

    String resetPassword(String email);

    UserResponse updateProfileImage(MultipartFile profileImage) throws IOException;

    User findUserByUsernameInDB(String username);

    MessageResponse deleteProfileImage() throws IOException;

    byte[] getProfileImage(String username, String fileName) throws IOException;

}
