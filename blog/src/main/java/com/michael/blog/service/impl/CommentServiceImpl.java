package com.michael.blog.service.impl;

import com.michael.blog.entity.Comment;
import com.michael.blog.entity.Post;
import com.michael.blog.entity.User;
import com.michael.blog.exception.payload.CommentNotFoundException;
import com.michael.blog.exception.payload.PostNotFoundException;
import com.michael.blog.payload.request.CommentRequest;
import com.michael.blog.payload.response.CommentResponse;
import com.michael.blog.payload.response.MessageResponse;
import com.michael.blog.repository.CommentRepository;
import com.michael.blog.repository.PostRepository;
import com.michael.blog.service.CommentService;
import com.michael.blog.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final ModelMapper mapper;
    private final UserService userService;

    public CommentServiceImpl(CommentRepository commentRepository,
                              PostRepository postRepository,
                              ModelMapper mapper,
                              UserService userService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.mapper = mapper;
        this.userService = userService;
    }

    @Override
    public CommentResponse createComment(Long postId, CommentRequest commentRequest) {
        Post post = getPostFromDB(postId);
        User user = userService.getLoggedInUser();
        Comment comment = Comment.builder()
                .username(user.getUsername())
                .body(commentRequest.getBody())
                .post(post)
                .user(user)
                .likes(0)
                .build();
        comment = commentRepository.save(comment);
        return mapper.map(comment, CommentResponse.class);
    }

    @Override
    public List<CommentResponse> getAllCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId)
                .stream()
                .map(comment -> mapper.map(comment, CommentResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponse getCommentById(Long postId, Long commentId) {
        Post post = getPostFromDB(postId);
        Comment comment = getCommentFromDB(commentId);
        isCommentBelongPost(post, comment);
        return mapper.map(comment, CommentResponse.class);
    }

    @Override
    public CommentResponse updateComment(Long postId, Long commentId, CommentRequest commentRequest) {
        Post post = getPostFromDB(postId);
        Comment comment = getCommentFromDB(commentId);
        isCommentBelongPost(post, comment);
        isCommentBelongUser(comment);
        comment.setBody(commentRequest.getBody());
        comment = commentRepository.save(comment);
        return mapper.map(comment, CommentResponse.class);
    }


    @Override
    public MessageResponse deleteComment(Long postId, Long commentId) {
        Post post = getPostFromDB(postId);
        Comment comment = getCommentFromDB(commentId);
        isCommentBelongPost(post, comment);
        isCommentBelongUser(comment);
        commentRepository.delete(comment);
        return new MessageResponse(String.format("Comment with id: %s was deleted", commentId));
    }

    @Override
    public Comment getCommentFromDB(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(String.format("Comment with id %s not found", commentId)));
    }

    @Override
    public void isCommentBelongPost(Post post, Comment comment) {
        if (!comment.getPost().getId().equals(post.getId())) {
            throw new RuntimeException("Comment does not belong to post");
        }
    }

    @Override
    public void isCommentBelongUser(Comment comment) {
        if (!comment.getUser().getId().equals(userService.getLoggedInUser().getId())) {
            throw new RuntimeException("This comment doesn't belong to you, you can't delete it!");
        }
    }

    @Override
    public CommentResponse likeComment(Long commentId) {
        User user = userService.getLoggedInUser();
        Comment comment = getCommentFromDB(commentId);

        Optional<String> userLiked = comment.getLikedUsers()
                .stream()
                .filter(u -> u.equals(user.getUsername()))
                .findAny();

        if (userLiked.isPresent()) {
            comment.setLikes(comment.getLikes() - 1);
            comment.getLikedUsers().remove(user.getUsername());
        } else {
            comment.setLikes(comment.getLikes() + 1);
            comment.getLikedUsers().add(user.getUsername());
        }
        comment = commentRepository.save(comment);
        return mapper.map(comment, CommentResponse.class);
    }

    private Post getPostFromDB(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(String.format("Post with id: %s not found", postId)));
    }

}
