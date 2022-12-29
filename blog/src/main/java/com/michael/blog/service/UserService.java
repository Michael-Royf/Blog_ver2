package com.michael.blog.service;

import com.michael.blog.entity.User;
import com.michael.blog.payload.request.LoginRequest;
import com.michael.blog.payload.request.UserRequest;
import com.michael.blog.payload.response.UserResponse;

public interface UserService {
    String login(LoginRequest loginRequest);

    String register(UserRequest registerRequest);

    UserResponse getUserById(Long id);

    UserResponse updateUser(Long id, UserRequest registerRequest);

    String deleteUser(Long id);

    User getLoggedInUser();
}
