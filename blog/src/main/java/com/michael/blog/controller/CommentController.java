package com.michael.blog.controller;

import com.michael.blog.payload.request.CommentRequest;
import com.michael.blog.payload.response.CommentResponse;
import com.michael.blog.payload.response.MessageResponse;
import com.michael.blog.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("post/{postId}/comment")
    public ResponseEntity<CommentResponse> createComment(@PathVariable long postId,
                                                         @RequestBody @Valid CommentRequest commentRequest) {
        return new ResponseEntity<>(commentService.createComment(postId, commentRequest), CREATED);
    }

    @GetMapping("/post/{postId}/comment")
    public ResponseEntity<List<CommentResponse>> getCommentByPostId(@PathVariable Long postId) {
        return new ResponseEntity<>(commentService.getCommentByPostId(postId), OK);
    }

    @GetMapping("/post/{postId}/comment/{commentId}")
    public ResponseEntity<CommentResponse> getCommentById(@PathVariable(value = "postId") Long postId,
                                                          @PathVariable(value = "commentId") Long commentId) {
        return new ResponseEntity<>(commentService.getCommentById(postId, commentId), OK);
    }


    @PutMapping("/post/{postId}/comment/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable(value = "postId") Long postId,
                                                         @PathVariable(value = "commentId") Long commentId,
                                                         @RequestBody @Valid CommentRequest commentRequest) {
        return new ResponseEntity<>(commentService.updateComment(postId, commentId, commentRequest), OK);
    }

    @DeleteMapping("/post/{postId}/comment/{commentId}")
    public ResponseEntity<MessageResponse> deleteComment(@PathVariable(value = "postId") Long postId,
                                                         @PathVariable(value = "commentId") Long commentId) {
        return new ResponseEntity<>(commentService.deleteComment(postId, commentId), OK);
    }

}
