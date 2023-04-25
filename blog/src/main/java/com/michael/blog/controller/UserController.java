package com.michael.blog.controller;

import com.michael.blog.payload.request.EmailRequest;
import com.michael.blog.payload.request.LoginRequest;
import com.michael.blog.payload.request.PasswordRequest;
import com.michael.blog.payload.request.UserRequest;
import com.michael.blog.payload.response.JwtAuthResponse;
import com.michael.blog.payload.response.MessageResponse;
import com.michael.blog.payload.response.UserResponse;
import com.michael.blog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping("/api/v1/")
@Tag(name = "CRUD REST APIs for User Resource")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @Operation(
            summary = "Create User Rest API",
            description = "Create User REST API is used to save user into database")
    @ApiResponse(
            responseCode = "201",
            description = "Http Status 201 CREATED")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid UserRequest registerRequest) throws IOException {
        return new ResponseEntity<>(userService.register(registerRequest), HttpStatus.CREATED);
    }


    @Operation(
            summary = "Login Rest API",
            description = "Login REST API is used to login")
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS")
    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        return ResponseEntity.ok(userService.login(loginRequest));
    }

    @PostMapping("/refesh_token")
    public JwtAuthResponse refreshToken(HttpServletRequest request,
                                        HttpServletResponse response) {
        return userService.refreshToken(request, response);
    }


    @GetMapping(path = "/registration/confirm")
    public ResponseEntity<String> confirm(@RequestParam("token") String token) {
        return new ResponseEntity<>(userService.confirmToken(token), HttpStatus.OK);
    }

    @Operation(
            summary = "Get User Profile By Id  Rest API",
            description = "Get User Profile By Id REST API is used to fetch  user profile by Id from the database")
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS")
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable Long userId) {
        return new ResponseEntity<>(userService.getUserById(userId), HttpStatus.OK);
    }

    @Operation(
            summary = "Get User Profile  Rest API",
            description = "Get User Profile  REST API is used to fetch  user profile  from the database")
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS")
    @GetMapping("/user/myprofile")
    public ResponseEntity<UserResponse> getMyProfile() {
        return new ResponseEntity<>(userService.getMyProfile(), HttpStatus.OK);

    }

    @Operation(
            summary = "Delete User Rest API",
            description = "Delete User REST API is used to delete User profile")
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS")
    @DeleteMapping("/user")
    public ResponseEntity<String> deleteUser() {
        return new ResponseEntity<>(userService.deleteUser(), HttpStatus.OK);
    }

    @Operation(
            summary = "Deactivate User Rest API",
            description = "Deactivate User REST API is used to deactivate User profile")
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS")
    @GetMapping("user/deactivateprofile")
    public ResponseEntity<String> deactivateProfile() {
        return new ResponseEntity<>(userService.deactivateProfile(), HttpStatus.OK);
    }

    @Operation(
            summary = "Change Password Rest API",
            description = "Change Password REST API is used to change password and send it to email")
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS")
    @PostMapping("/user/changepassword")
    public ResponseEntity<String> changePassword(@RequestBody @Valid PasswordRequest passwordRequest) {
        return new ResponseEntity<>(userService.changePassword(passwordRequest.getOldPassword(),
                passwordRequest.getNewPassword()), HttpStatus.OK);
    }

    @Operation(
            summary = "Generate New Password(if the user has forgotten their password)  Rest API",
            description = "Generate New Password REST API is used to generate a new password and send it to email if the user has forgotten their password")
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS")
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody @Valid EmailRequest email) {
        return new ResponseEntity<>(userService.forgotPassword(email.getEmail()), HttpStatus.OK);
    }


    @GetMapping(path = "user/image/{username}/{filename}", produces = IMAGE_JPEG_VALUE)
    public ResponseEntity<?> getProfileImage(@PathVariable("username") String username,
                                             @PathVariable("filename") String fileName) throws IOException {
        byte[] profileImage = userService.getProfileImage(username, fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(IMAGE_JPEG_VALUE))
                .body(new ByteArrayResource(profileImage));
    }

    @PostMapping("/updateProfileImage")
    public ResponseEntity<UserResponse> updateProfileImage(@RequestParam(value = "profileImage") MultipartFile profileImage) throws IOException {
        return new ResponseEntity<>(userService.updateProfileImage(profileImage), HttpStatus.OK);
    }


    @DeleteMapping("/deleteProfileImage")
    public ResponseEntity<MessageResponse> deleteProfileImage() throws IOException {
        return new ResponseEntity<>(userService.deleteProfileImage(), HttpStatus.OK);
    }


}
