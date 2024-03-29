package com.michael.blog.service;

import com.michael.blog.entity.Comment;
import com.michael.blog.entity.Post;
import com.michael.blog.payload.request.CommentRequest;
import com.michael.blog.payload.response.CommentResponse;
import com.michael.blog.payload.response.MessageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CommentService {

    CommentResponse createComment(Long postId, CommentRequest commentRequest);

    List<CommentResponse> getAllCommentsByPostId(Long postId);

    CommentResponse getCommentById(Long postId, Long commentId);

    CommentResponse updateComment(Long postId, Long commentId, CommentRequest commentRequest);

    MessageResponse deleteComment(Long postId, Long commentId);

    Comment getCommentFromDB(Long commentId);

    void isCommentBelongPost(Post post, Comment comment);

    void isCommentBelongUser(Comment comment);

    CommentResponse likeComment(Long commentId);


    CommentResponse addImageToComment(Long postId, Long commentId, List<MultipartFile> files) throws IOException;

    byte[] viewCommentImage(String username, Long postId, Long commentId, String filename);

    MessageResponse deleteCommentImage(Long postId, Long commentId, String filename);

    MessageResponse deleteAllCommentImages(Long postId, Long commentId);
}
