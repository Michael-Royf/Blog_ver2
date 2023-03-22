package com.michael.blog.controller;

import com.michael.blog.service.impl.AdminServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1")
@Tag(
        name = "REST APIs for Admin Resource"
)
public class AdminController {
    @Autowired
    private AdminServiceImpl adminService;

    @SecurityRequirement(
            name = "Bear Authentication"
    )

    //  @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete User Rest API",
            description = "Delete User REST API is used to delete User profile")
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS")
    @DeleteMapping("/admin/deleteuser/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        return new ResponseEntity<>(adminService.deleteUser(userId), HttpStatus.OK);
    }

    //  @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Enable User Rest API",
            description = "Enable User REST API is used to enable User profile")
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS")
    @PostMapping("/admin/enableuser/{userId}")
    public ResponseEntity<String> enableUserProfile(@PathVariable Long userId) {
        return new ResponseEntity<>(adminService.activationUserProfile(userId), HttpStatus.OK);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Disable User Rest API",
            description = "Disable User REST API is used to disable User profile")
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS")
    @PostMapping("/admin/disableuser/{userId}")
    public ResponseEntity<String> disableUserProfile(@PathVariable Long userId) {
        return new ResponseEntity<>(adminService.deactivationUserProfile(userId), HttpStatus.OK);
    }

    //   @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete Post Rest API",
            description = "Delete Post REST API is used to delete post by id")
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS")
    @PostMapping("/admin/post/delete/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        return new ResponseEntity<>(adminService.deletePost(postId), HttpStatus.OK);
    }

    //  @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete Comment Rest API",
            description = "Delete Comment REST API is used to delete comment by id")
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS")
    @PostMapping("/admin/comment/delete/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        return new ResponseEntity<>(adminService.deleteComment(commentId), HttpStatus.OK);
    }

    @Operation(
            summary = "Change UserRole to AdminRole Rest API",
            description = "Change UserRole to AdminRole REST API is used to change user role to admin role ")
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS")
    @PostMapping("/superadmin/change_role_to_admin/{userId}")
    public ResponseEntity<String> changeUserRoleToAdmin(@PathVariable Long userId) {
        return new ResponseEntity<>(adminService.changeUserRoleToAdmin(userId), HttpStatus.OK);
    }

    @Operation(
            summary = "Change AdminRole to UserRole Rest API",
            description = "Change AdminRole to UserRole REST API is used to change admin role to user role ")
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS")
    @PostMapping("/superadmin/change_role_to_user/{userId}")
    public ResponseEntity<String> changeAdminRoleToUser(@PathVariable Long userId) {
        return new ResponseEntity<>(adminService.changeAdminRoleToUser(userId), HttpStatus.OK);
    }


}
