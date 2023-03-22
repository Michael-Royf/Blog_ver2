package com.michael.blog.controller;

import com.michael.blog.payload.request.CommentRequest;
import com.michael.blog.payload.response.CommentResponse;
import com.michael.blog.payload.response.MessageResponse;
import com.michael.blog.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1")
@Tag(
        name = "CRUD REST APIs for Comment Resource"
)
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Operation(
            summary = "Create Comment Rest API",
            description = "Create Comment REST API is used to save comment into database")
    @ApiResponse(
            responseCode = "201",
            description = "Http Status 201 CREATED")
    @PostMapping("post/{postId}/comment")
    public ResponseEntity<CommentResponse> createComment(@PathVariable long postId,
                                                         @RequestBody @Valid CommentRequest commentRequest) {
        return new ResponseEntity<>(commentService.createComment(postId, commentRequest), CREATED);
    }

    @Operation(
            summary = "Get Comments By Post Id Rest API",
            description = "Get Comments By Post Id REST API is used to fetch all comments by post from the database")
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS")
    @GetMapping("/post/{postId}/comment")
    public ResponseEntity<List<CommentResponse>> getCommentsByPostId(@PathVariable Long postId) {
        return new ResponseEntity<>(commentService.getCommentByPostId(postId), OK);
    }

    @Operation(
            summary = "Get Comment By Id Rest API",
            description = "Get Comment By Id REST API is used to fetch single comment by Id from the database")
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS")
    @GetMapping("/post/{postId}/comment/{commentId}")
    public ResponseEntity<CommentResponse> getCommentById(@PathVariable(value = "postId") Long postId,
                                                          @PathVariable(value = "commentId") Long commentId) {
        return new ResponseEntity<>(commentService.getCommentById(postId, commentId), OK);
    }

    @Operation(
            summary = "Update Comment  Rest API",
            description = "Update Comment REST API is used to update comment by Id from the database")
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS")
    @PutMapping("/post/{postId}/comment/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable(value = "postId") Long postId,
                                                         @PathVariable(value = "commentId") Long commentId,
                                                         @RequestBody @Valid CommentRequest commentRequest) {
        return new ResponseEntity<>(commentService.updateComment(postId, commentId, commentRequest), OK);
    }

    @Operation(
            summary = "Delete Comment  Rest API",
            description = "Delete Comment REST API is used to delete particular comment from the database")
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @DeleteMapping("/post/{postId}/comment/{commentId}")
    public ResponseEntity<MessageResponse> deleteComment(@PathVariable(value = "postId") Long postId,
                                                         @PathVariable(value = "commentId") Long commentId) {
        return new ResponseEntity<>(commentService.deleteComment(postId, commentId), OK);
    }

}
