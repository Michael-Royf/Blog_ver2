package com.michael.blog.controller;

import com.michael.blog.payload.request.PostRequest;
import com.michael.blog.payload.response.MessageResponse;
import com.michael.blog.payload.response.PostResponse;
import com.michael.blog.payload.response.PostResponsePagination;
import com.michael.blog.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.michael.blog.constants.PaginationConstants.*;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "CRUD REST APIs for Post Resource")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @Operation(
            summary = "Create Post Rest API",
            description = "Create Post REST API is used to save post into database")
    @ApiResponse(
            responseCode = "201",
            description = "Http Status 201 CREATED")
    @PostMapping("/post")
    public ResponseEntity<PostResponse> createPost(@RequestBody @Valid PostRequest postRequest) {
        return new ResponseEntity<>(postService.createPost(postRequest), CREATED);
    }

    @Operation(
            summary = "Get All Posts  Rest API",
            description = "Get All Posts  REST API is used to fetch all posts from the database")
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS")
    @GetMapping("/post")
    public ResponseEntity<PostResponsePagination> getAllPosts(
            @RequestParam(value = "pageNo", defaultValue = DEFAULT_PAGE_NUMBER, required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = DEFAULT_SORT_DIRECTION, required = false) String sortDir) {
        return new ResponseEntity<>(postService.getAllPosts(page, pageSize, sortBy, sortDir), OK);
    }

    @Operation(
            summary = "Get Post By Id Rest API",
            description = "Get Post By Id REST API is used to get single post from the database")
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS")
    @GetMapping("/post/{postId}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long postId) {
        return new ResponseEntity<>(postService.getPostById(postId), OK);
    }

    @Operation(
            summary = "Delete Post  Rest API",
            description = "Delete Post REST API is used to delete particular post from the database")
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @DeleteMapping("/post/{postId}")
    public ResponseEntity<MessageResponse> deletePost(@PathVariable Long postId) {
        return new ResponseEntity<>(postService.deletePost(postId), OK);
    }

    @Operation(
            summary = "Update Post  Rest API",
            description = "Update Post REST API is used to updated particular post from the database")
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @PutMapping("/post/{postId}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long postId, @RequestBody PostRequest postRequest) {
        return new ResponseEntity<>(postService.updatePost(postId, postRequest), OK);
    }

    @Operation(
            summary = "Get Only Users Post  Rest API",
            description = "Get Users Posts REST API is used to fetch all users posts from the database")
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS")
    @GetMapping("/myposts")
    public ResponseEntity<List<PostResponse>> getMyPosts() {
        return new ResponseEntity<>(postService.getMyPosts(), OK);
    }

    @Operation(
            summary = "Get Posts By Category Rest API",
            description = "Get Posts By Category REST API is used to fetch all posts by category from the database")
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS")
    @GetMapping("/posts/{categoryId}")
    public ResponseEntity<List<PostResponse>> getPostsByCategory(@PathVariable("categoryId") Long categoryId) {
        return new ResponseEntity<>(postService.getPostsByCategory(categoryId), OK);
    }

    @PostMapping("/post/like/{postId}")
    public ResponseEntity<PostResponse> likePost(@PathVariable Long postId) {
        return new ResponseEntity<>(postService.likePost(postId), OK);
    }


    @PostMapping("/post/addImage/{postId}")
    public ResponseEntity<PostResponse> addImagesToPost(@PathVariable("postId") Long postId,
                                                        @RequestParam(value = "files") List<MultipartFile> files) throws IOException {
        return new ResponseEntity<>(postService.addImageToPost(postId, files), OK);
    }


    @GetMapping(path = "/post/image/{username}/{postId}/{filename}", produces = IMAGE_JPEG_VALUE)
    public ResponseEntity<?> getPostImage(@PathVariable("username") String username,
                                          @PathVariable("postId") Long postId,
                                          @PathVariable("filename") String filename) {
        byte[] postImage = postService.viewPostImage(username, postId, filename);
        HttpHeaders headers = new HttpHeaders();
        headers.add("File-Name", filename);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;File-Name=" + filename);

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(IMAGE_JPEG_VALUE))
                .headers(headers)
                .body(postImage);
    }

    //TODO: don't work
    @GetMapping(path = "/post/{postId}/images", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<List<Resource>> viewAllImagesFromPost(@PathVariable("postId") Long postId) {
        List<Resource> resources = new ArrayList<>();
        for (byte[] bytes : postService.viewAllPostImages(postId)) {
            ByteArrayResource byteArrayResource = new ByteArrayResource(bytes);
            resources.add(byteArrayResource);
        }

        if (!resources.isEmpty()) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resources);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/post/image/{postId}/{filename}")
    public ResponseEntity<MessageResponse> deletePostImage(@PathVariable("filename") String filename,
                                                           @PathVariable("postId") Long postId) {
        return new ResponseEntity<>(postService.deletePostImage(postId, filename), OK);
    }


    @DeleteMapping("/post/image/{postId}")
    public ResponseEntity<MessageResponse> deleteAllPostImages(@PathVariable("postId") Long postId) {
        return new ResponseEntity<>(postService.deleteAllPostImages(postId), OK);
    }


}
