package com.michael.blog.service.impl;

import com.michael.blog.entity.Comment;
import com.michael.blog.entity.Post;
import com.michael.blog.entity.User;
import com.michael.blog.payload.request.CommentRequest;
import com.michael.blog.payload.response.CommentResponse;
import com.michael.blog.payload.response.MessageResponse;
import com.michael.blog.repository.CommentRepository;
import com.michael.blog.repository.PostRepository;
import com.michael.blog.service.CommentService;
import com.michael.blog.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {


    private CommentRepository commentRepository;
    private PostRepository postRepository;
    private ModelMapper mapper;
    private UserService userService;

    @Autowired
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
    public CommentResponse createComment(long postId, CommentRequest commentRequest) {
        Post post = getPostFromDB(postId);
        User user = userService.getLoggedInUser();
        Comment comment = Comment.builder()
                .username(user.getUsername())
                .body(commentRequest.getBody())
                .post(post)
                .user(user)
                .build();
        comment = commentRepository.save(comment);
        return mapper.map(comment, CommentResponse.class);
    }

    @Override
    public List<CommentResponse> getCommentByPostId(long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        return comments.stream()
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
        if (!comment.getUser().getId().equals(userService.getLoggedInUser().getId())){
            throw  new RuntimeException("This comment doesn't belong to you, you can't update it!");
        }
        comment.setBody(commentRequest.getBody());
        comment = commentRepository.save(comment);
        return mapper.map(comment, CommentResponse.class);
    }


    @Override
    public MessageResponse deleteComment(Long postId, Long commentId) {
        Post post = getPostFromDB(postId);
        Comment comment = getCommentFromDB(commentId);
        isCommentBelongPost(post, comment);
        if (!comment.getUser().getId().equals(userService.getLoggedInUser().getId())){
            throw  new RuntimeException("This comment doesn't belong to you, you can't delete it!");
        }
        commentRepository.delete(comment);
        return new MessageResponse(String.format("Comment with id: %s was deleted", commentId));
    }


    private Comment getCommentFromDB(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Comment with id %s not found", commentId)));
    }

    private Post getPostFromDB(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Post with id: %s not found", postId)));
    }

    private void isCommentBelongPost(Post post, Comment comment) {
        if (!comment.getPost().getId().equals(post.getId())) {
            throw new RuntimeException("Comment does not belong to post");
        }
    }

}
