package com.michael.blog.service.impl;

import com.michael.blog.constants.UserConstant;
import com.michael.blog.entity.Comment;
import com.michael.blog.entity.Post;
import com.michael.blog.entity.User;
import com.michael.blog.entity.enumeration.UserRole;
import com.michael.blog.exception.payload.UserNotFoundException;
import com.michael.blog.repository.CommentRepository;
import com.michael.blog.repository.PostRepository;
import com.michael.blog.repository.UserRepository;
import com.michael.blog.service.AdminService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public AdminServiceImpl(PostRepository postRepository,
                            UserRepository userRepository,
                            CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public String deleteUser(Long userId) {
        User user = getUserFromDbById(userId);
        if (!user.getRole().name().equals(UserRole.ROLE_USER.name())) {
            throw new RuntimeException("You cannot delete this account");
        }
        userRepository.delete(user);
        return String.format("User with username:  %s was deleted", user.getUsername());
    }

    @Override
    public String activationUserProfile(Long userId) {
        User user = getUserFromDbById(userId);
        if (!user.getRole().name().equals(UserRole.ROLE_USER.name())) {
            throw new RuntimeException("You cannot activation this account");
        }
        userRepository.enableUser(user.getEmail());
        return String.format("User profile with username: %s was activation", userId);
    }

    @Override
    public String deactivationUserProfile(Long userId) {
        User user = getUserFromDbById(userId);
        if (!user.getRole().name().equals(UserRole.ROLE_USER.name())) {
            throw new RuntimeException("You cannot deactivation this account");
        }
        userRepository.disabledUser(user.getEmail());
        return String.format("User profile with username: %s was disabled", user.getUsername());
    }

    @Override
    public String deletePost(Long postId) {
        Post post = getPostFromDB(postId);
        postRepository.delete(post);
        return String.format("Post with id: %s was deleted", postId);
    }

    @Override
    public String deleteComment(Long commentId) {
        Comment comment = getCommentFromDB(commentId);
        commentRepository.delete(comment);
        return String.format("Comment with id: %s was deleted", commentId);
    }

    @Override
    public String changeUserRoleToAdmin(Long userId) {
        User user = getUserFromDbById(userId);
        user.setRole(UserRole.ROLE_ADMIN);
        userRepository.save(user);
        return String.format("The account with the username %s is assigned the role of the Admin", user.getUsername());
    }

    @Override
    public String changeAdminRoleToUser(Long userId) {
        User user = getUserFromDbById(userId);
        user.setRole(UserRole.ROLE_USER);
        userRepository.save(user);
        return String.format("The account with the username %s is assigned the role of the User", user.getUsername());
    }


    private User getUserFromDbById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format(UserConstant.NO_USER_FOUND_BY_ID, userId)));
    }

    private Post getPostFromDB(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Post with id: %s not found", postId)));
    }

    private Comment getCommentFromDB(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Comment with id %s not found", commentId)));
    }
}
