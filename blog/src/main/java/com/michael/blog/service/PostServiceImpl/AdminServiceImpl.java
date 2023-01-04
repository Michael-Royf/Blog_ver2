package com.michael.blog.service.PostServiceImpl;

import com.michael.blog.constants.UserConstant;
import com.michael.blog.entity.Comment;
import com.michael.blog.entity.Post;
import com.michael.blog.entity.User;
import com.michael.blog.exception.payload.UserNotFoundException;
import com.michael.blog.repository.CommentRepository;
import com.michael.blog.repository.PostRepository;
import com.michael.blog.repository.UserRepository;
import com.michael.blog.service.AdminService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    private PostRepository postRepository;
    private UserRepository userRepository;
    private CommentRepository commentRepository;

    @Autowired
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
        userRepository.delete(user);
        return String.format("User with username:  %s was deleted", user.getUsername());
    }

    @Override
    public String activationUserProfile(Long userId) {
        User user = getUserFromDbById(userId);
        userRepository.enableUser(user.getEmail());
        return String.format("User profile with username: %s was activation", userId);
    }

    @Override
    public String deactivationUserProfile(Long userId) {
        User user = getUserFromDbById(userId);
        userRepository.disabledUser(user.getEmail());
        return String.format("User profile with username: %s was disabled", user.getUsername());
    }

    @Override
    public String deletePost(Long postId) {
        Post post =getPostFromDB(postId);
        postRepository.delete(post);
        return String.format("Post with id: %s was deleted", postId);
    }

    @Override
    public String deleteComment(Long commentId) {
        Comment comment = getCommentFromDB(commentId);
        commentRepository.delete(comment);
        return String.format("Comment with id: %s was deleted", commentId);
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
