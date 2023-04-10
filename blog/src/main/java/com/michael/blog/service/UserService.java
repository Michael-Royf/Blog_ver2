package com.michael.blog.service;

import com.michael.blog.entity.User;
import com.michael.blog.payload.request.LoginRequest;
import com.michael.blog.payload.request.UserRequest;
import com.michael.blog.payload.response.JwtAuthResponse;
import com.michael.blog.payload.response.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    JwtAuthResponse login(LoginRequest loginRequest);

    JwtAuthResponse refreshToken(HttpServletRequest request, HttpServletResponse response);

    String register(UserRequest registerRequest);

    UserResponse getUserById(Long id);

    UserResponse getMyProfile();

    UserResponse updateUser(UserRequest registerRequest);

    String deleteUser();

    String deactivateProfile();

    User getLoggedInUser();

    String changePassword(String oldPassword, String newPassword);

    String confirmToken(String token);

    String forgotPassword(String email);


}
