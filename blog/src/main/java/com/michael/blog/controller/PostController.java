package com.michael.blog.controller;

import com.michael.blog.constants.PaginationConstants.*;
import com.michael.blog.payload.request.PostRequest;
import com.michael.blog.payload.response.MessageResponse;
import com.michael.blog.payload.response.PostResponse;
import com.michael.blog.payload.response.PostResponsePagination;
import com.michael.blog.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.michael.blog.constants.PaginationConstants.*;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1")
public class PostController {

    @Autowired
    private PostService postService;


    @PostMapping("/post")
    public ResponseEntity<PostResponse> createPost(@RequestBody @Valid PostRequest postRequest) {
        return new ResponseEntity<>(postService.createPost(postRequest), CREATED);
    }

    @GetMapping("/post")
    public ResponseEntity<PostResponsePagination> getAllPosts(
            @RequestParam(value = "pageNo", defaultValue = DEFAULT_PAGE_NUMBER, required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = DEFAULT_SORT_DIRECTION, required = false) String sortDir) {
        return new ResponseEntity<>(postService.getAllPosts(page, pageSize, sortBy, sortDir), OK);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long postId) {
        return new ResponseEntity<>(postService.getPostById(postId), OK);
    }

    @DeleteMapping("/post/{postId}")
    public ResponseEntity<MessageResponse> deletePost(@PathVariable Long postId) {
        return new ResponseEntity<>(postService.deletePost(postId), OK);
    }

    @PutMapping("/post/{postId}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long postId, @RequestBody PostRequest postRequest) {
        return new ResponseEntity<>(postService.updatePost(postId, postRequest), OK);
    }

    @GetMapping("/myposts")
    public ResponseEntity<List<PostResponse>> getMyPosts(){
        return new ResponseEntity<>(postService.getMyPosts(), OK);
    }


}
