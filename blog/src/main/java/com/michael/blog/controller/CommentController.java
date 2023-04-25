package com.michael.blog.controller;

import com.michael.blog.payload.request.CommentRequest;
import com.michael.blog.payload.response.CommentResponse;
import com.michael.blog.payload.response.MessageResponse;
import com.michael.blog.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "CRUD REST APIs for Comment Resource")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }


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
    public ResponseEntity<List<CommentResponse>> getAllCommentsByPostId(@PathVariable Long postId) {
        return new ResponseEntity<>(commentService.getAllCommentsByPostId(postId), OK);
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

    @PostMapping("/comment/like/{commentId}")
    public ResponseEntity<CommentResponse> likeComment(@PathVariable Long commentId) {
        return new ResponseEntity<>(commentService.likeComment(commentId), OK);
    }

    @PostMapping("/post/{postId}/comment/{commentId}/addImages")
    public ResponseEntity<CommentResponse> addImagesToComment(@PathVariable("postId") Long postId,
                                                              @PathVariable("commentId") Long commentId,
                                                              @RequestParam("files") List<MultipartFile> files) throws IOException {
        return new ResponseEntity<>(commentService.addImageToComment(postId, commentId, files), OK);
    }


    @GetMapping(path = "/post/{postId}/{username}/comment/{commentId}/image/{filename}", produces = IMAGE_JPEG_VALUE)
    public ResponseEntity<?> viewCommentImage(@PathVariable("postId") Long postId,
                                              @PathVariable("commentId") Long commentId,
                                              @PathVariable("filename") String filename,
                                              @PathVariable("username") String username) {
        byte[] postImage = commentService.viewCommentImage(username, postId, commentId, filename);
        HttpHeaders headers = new HttpHeaders();
        headers.add("File-Name", filename);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;File-Name=" + filename);

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(IMAGE_JPEG_VALUE))
                .headers(headers)
                .body(postImage);

    }

    @DeleteMapping("/post/{postId}/comment/{commentId}/image/{filename}/delete")
    public ResponseEntity<MessageResponse> deleteCommentImage(@PathVariable("postId") Long postId,
                                                              @PathVariable("commentId") Long commentId,
                                                              @PathVariable("filename") String filename) {
        return new ResponseEntity<>(commentService.deleteCommentImage(postId, commentId, filename), OK);
    }

    @DeleteMapping("/post/{postId}/comment/{commentId}/deleteAll")
    public ResponseEntity<MessageResponse> deleteAllImagesFromComment(@PathVariable("commentId") Long commentId,
                                                                      @PathVariable("postId") Long postId) {
        return new ResponseEntity<>(commentService.deleteAllCommentImages(postId, commentId), OK);
    }

}
