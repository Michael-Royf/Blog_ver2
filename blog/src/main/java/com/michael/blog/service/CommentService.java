package com.michael.blog.service;

import com.michael.blog.payload.request.CommentRequest;
import com.michael.blog.payload.response.CommentResponse;
import com.michael.blog.payload.response.MessageResponse;

import java.util.List;

public interface CommentService {

    CommentResponse createComment(long postId, CommentRequest commentRequest);

    List<CommentResponse> getCommentByPostId(long postId);

    CommentResponse getCommentById(Long postId, Long commentId);

    CommentResponse updateComment(Long postId, Long commentId, CommentRequest commentRequest);

    MessageResponse deleteComment(Long postId, Long commentId);

}
