package com.michael.blog.controller;

import com.michael.blog.payload.request.EmailRequest;
import com.michael.blog.payload.request.LoginRequest;
import com.michael.blog.payload.request.PasswordRequest;
import com.michael.blog.payload.request.UserRequest;
import com.michael.blog.payload.response.JwtAuthResponse;
import com.michael.blog.payload.response.UserResponse;
import com.michael.blog.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/")
public class UserController {
    @Autowired
    private UserService userService;


    @PostMapping(value = {"/login", "/signin"})
    public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginRequest loginRequest) {
        String token = userService.login(loginRequest);
        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setAccessToken(token);
        return ResponseEntity.ok(jwtAuthResponse);
    }

    @PostMapping(value = {"/register", "/signup"})
    public ResponseEntity<String> register(@RequestBody @Valid UserRequest registerRequest) {
        return new ResponseEntity<>(userService.register(registerRequest), HttpStatus.CREATED);
    }


    @GetMapping(path = "/registration/confirm")
    public ResponseEntity<String> confirm(@RequestParam("token") String token) {
        return new ResponseEntity<>(userService.confirmToken(token), HttpStatus.OK);
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable Long userId) {
        return new ResponseEntity<>(userService.getUserById(userId), HttpStatus.OK);
    }

    @GetMapping("/user/myprofile")
    public ResponseEntity<UserResponse> getMyProfile() {
        return new ResponseEntity<>(userService.getMyProfile(), HttpStatus.OK);
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody @Valid EmailRequest email) {
        return new ResponseEntity<>(userService.forgotPassword(email.getEmail()), HttpStatus.OK);
    }

    @DeleteMapping("/user")
    public ResponseEntity<String> deleteUser() {
        return new ResponseEntity<>(userService.deleteUser(), HttpStatus.OK);
    }


    @GetMapping("user/deactivateprofile")
    public ResponseEntity<String> deactivateProfile() {
        return new ResponseEntity<>(userService.deactivateProfile(), HttpStatus.OK);
    }

    @PostMapping("/user/changepassword")
    public ResponseEntity<String> changePassword(@RequestBody PasswordRequest passwordRequest) {
        return new ResponseEntity<>(userService.changePassword(passwordRequest.getOldPassword(),
                passwordRequest.getNewPassword()), HttpStatus.OK);
    }
}
