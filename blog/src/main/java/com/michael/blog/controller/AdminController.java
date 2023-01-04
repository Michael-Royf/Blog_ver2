package com.michael.blog.controller;

import com.michael.blog.service.PostServiceImpl.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    @Autowired
    private AdminServiceImpl adminService;


    //  @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/deleteuser/{userId}")
    private ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        return new ResponseEntity<>(adminService.deleteUser(userId), HttpStatus.OK);
    }

    //  @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/enableuser/{userId}")
    private ResponseEntity<String> enableUserProfile(@PathVariable Long userId) {
        return new ResponseEntity<>(adminService.activationUserProfile(userId), HttpStatus.OK);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/disableuser/{userId}")
    private ResponseEntity<String> disableUserProfile(@PathVariable Long userId) {
        return new ResponseEntity<>(adminService.deactivationUserProfile(userId), HttpStatus.OK);
    }

 //   @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/post/delete/{postId}")
    private ResponseEntity<String> deletePost(@PathVariable Long postId) {
        return new ResponseEntity<>(adminService.deletePost(postId), HttpStatus.OK);
    }
  //  @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/comment/delete/{commentId}")
    private ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        return new ResponseEntity<>(adminService.deleteComment(commentId), HttpStatus.OK);
    }

}
