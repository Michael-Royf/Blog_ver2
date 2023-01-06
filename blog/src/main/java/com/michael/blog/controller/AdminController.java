package com.michael.blog.controller;

import com.michael.blog.service.impl.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1")
public class AdminController {
    @Autowired
    private AdminServiceImpl adminService;


    //  @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/deleteuser/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        return new ResponseEntity<>(adminService.deleteUser(userId), HttpStatus.OK);
    }

    //  @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/enableuser/{userId}")
    public ResponseEntity<String> enableUserProfile(@PathVariable Long userId) {
        return new ResponseEntity<>(adminService.activationUserProfile(userId), HttpStatus.OK);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/disableuser/{userId}")
    public ResponseEntity<String> disableUserProfile(@PathVariable Long userId) {
        return new ResponseEntity<>(adminService.deactivationUserProfile(userId), HttpStatus.OK);
    }

    //   @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/post/delete/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        return new ResponseEntity<>(adminService.deletePost(postId), HttpStatus.OK);
    }

    //  @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/comment/delete/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        return new ResponseEntity<>(adminService.deleteComment(commentId), HttpStatus.OK);
    }

    @PostMapping("/superadmin/change_role_to_admin/{userId}")
    public ResponseEntity<String> changeUserRoleToAdmin(@PathVariable Long userId) {
        return new ResponseEntity<>(adminService.changeUserRoleToAdmin(userId), HttpStatus.OK);
    }

    @PostMapping("/superadmin/change_role_to_user/{userId}")
    public ResponseEntity<String> changeAdminRoleToUser(@PathVariable Long userId) {
        return new ResponseEntity<>(adminService.changeAdminRoleToUser(userId), HttpStatus.OK);
    }


}
