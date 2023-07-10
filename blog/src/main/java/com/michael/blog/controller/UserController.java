package com.michael.blog.controller;

import com.michael.blog.payload.request.*;
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
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@Validated
@RestController
@RequestMapping("/api/v1/")
@Tag(name = "CRUD REST APIs for User Resource")
@RequiredArgsConstructor
public class UserController {

    //    private final HttpServletRequest request;
    private final UserService userService;

    @Operation(summary = "Create User Rest API",
            description = "Create User REST API is used to save user into database")
    @ApiResponse(responseCode = "201", description = "Http Status 201 CREATED")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid UserRequest registerRequest) throws IOException {
        return new ResponseEntity<>(userService.register(registerRequest), CREATED);
    }


    @Operation(summary = "Login Rest API",
            description = "Login REST API is used to login")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        return ResponseEntity.ok(userService.login(loginRequest));
    }
    @Operation(summary = "Refresh Token Rest API",
            description = "Refresh Token REST API is used to get a new access token")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PostMapping("/refesh_token")
    public JwtAuthResponse refreshToken(HttpServletRequest request,
                                        HttpServletResponse response) {
        return userService.refreshToken(request, response);
    }

    @Operation(summary = "Confirm REST API",
            description = "Confirm REST API is used to registration new user")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @GetMapping(path = "/registration/confirm")
    public ResponseEntity<String> confirm(@RequestParam("token") String token) {
        return new ResponseEntity<>(userService.confirmToken(token), OK);
    }

    @Operation(summary = "Get User Profile By Id  Rest API",
            description = "Get User Profile By Id REST API is used to fetch user profile by Id from the database")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable @Min(0) Long userId) {
        return new ResponseEntity<>(userService.getUserById(userId), OK);
    }

    @Operation(summary = "Get User Profile  Rest API",
            description = "Get User Profile  REST API is used to fetch  user profile  from the database")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @GetMapping("/user/myprofile")
    public ResponseEntity<UserResponse> getMyProfile() {
        return new ResponseEntity<>(userService.getMyProfile(), OK);
    }

    @Operation(summary = "Delete User Rest API",
            description = "Delete User REST API is used to delete User profile")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @DeleteMapping("/user")
    public ResponseEntity<String> deleteUser() {
        return new ResponseEntity<>(userService.deleteUser(), OK);
    }

    @Operation(summary = "Deactivate User Rest API",
            description = "Deactivate User REST API is used to deactivate User profile")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @GetMapping("user/deactivateprofile")
    public ResponseEntity<String> deactivateProfile() {
        return new ResponseEntity<>(userService.deactivateProfile(), OK);
    }

    @Operation(summary = "Change Password Rest API",
            description = "Change Password REST API is used to change password and send it to email")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PostMapping("/user/changepassword")
    public ResponseEntity<String> changePassword(@RequestBody @Valid PasswordChangeRequest passwordChangeRequest) {
        return new ResponseEntity<>(userService.changePassword(passwordChangeRequest), OK);
    }

    @Operation(summary = "Generate New Password(if the user has forgotten their password)  Rest API",
            description = "Generate New Password REST API is used to generate a new password and send it to email if the user has forgotten their password")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PostMapping("/forgot-password")
    public ResponseEntity<String> resetPassword(@RequestBody @Valid EmailRequest email) {
        return new ResponseEntity<>(userService.resetPassword(email.getEmail()), OK);
    }

    @Operation(summary = "Update user information  Rest API",
            description = "Used to update user information")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PutMapping("/user/update")
    public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UpdateUserRequest userRequest) {
        return new ResponseEntity<>(userService.updateUser(userRequest), OK);
    }


    @Operation(summary = "Get Profile Image  Rest API",
            description = "Get Profile Image REST API is used to fetch  user profile image from the database")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @GetMapping(path = "user/image/{username}/{filename}", produces = IMAGE_JPEG_VALUE)
    public ResponseEntity<?> getProfileImage(@PathVariable("username") String username,
                                             @PathVariable("filename") String fileName) throws IOException {
        byte[] profileImage = userService.getProfileImage(username, fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(IMAGE_JPEG_VALUE))
                .body(new ByteArrayResource(profileImage));
    }

    @Operation(summary = "Update Profile Image Rest API",
            description = "Update Profile Image REST API is used to update user profile image")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PostMapping("/updateProfileImage")
    public ResponseEntity<UserResponse> updateProfileImage(@RequestParam(value = "profileImage") MultipartFile profileImage) throws IOException {
        return new ResponseEntity<>(userService.updateProfileImage(profileImage), OK);
    }

    @Operation(summary = "Delete Profile Image Rest API",
            description = "Delete Profile Image REST API is used to delete user profile image from the database")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @DeleteMapping("/deleteProfileImage")
    public ResponseEntity<MessageResponse> deleteProfileImage() throws IOException {
        return new ResponseEntity<>(userService.deleteProfileImage(), OK);
    }
}
