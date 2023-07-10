package com.michael.blog.service;

import com.michael.blog.entity.Post;
import com.michael.blog.payload.request.PostRequest;
import com.michael.blog.payload.response.MessageResponse;
import com.michael.blog.payload.response.PostResponse;
import com.michael.blog.payload.response.PostResponsePagination;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PostService {
    PostResponse createPost(PostRequest postRequest);

    PostResponsePagination getAllPosts(int pageNo, int pageSiZe, String sortBy, String sortDir);

    //  List<PostResponse> getMyPosts(int pageNo, int pageSiZe, String sortBy, String sortDir);
    List<PostResponse> getMyPosts();

    PostResponse getPostById(Long postId);

    MessageResponse deletePost(Long postId);

    PostResponse updatePost(Long postId, PostRequest postRequest);

    List<PostResponse> getPostsByCategory(Long categoryId);

    List<PostResponse> searchPosts(String query);

    void isPostBelongUser(Post post);

    PostResponse likePost(Long postId);

    PostResponse addImageToPost(Long postId, List<MultipartFile> files) throws IOException;

    byte[] viewPostImage(String username, Long postId, String fileName);

    List<byte[]> viewAllPostImages(Long postId);

    MessageResponse deletePostImage(Long postId, String filename);

    MessageResponse deleteAllPostImages(Long postId);


}
