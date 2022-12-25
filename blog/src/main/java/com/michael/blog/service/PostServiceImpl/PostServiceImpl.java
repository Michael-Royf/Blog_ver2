package com.michael.blog.service.PostServiceImpl;

import com.michael.blog.entity.Post;
import com.michael.blog.payload.request.PostRequest;
import com.michael.blog.payload.response.MessageResponse;
import com.michael.blog.payload.response.PostResponse;
import com.michael.blog.payload.response.PostResponsePagination;
import com.michael.blog.repository.PostRepository;
import com.michael.blog.service.PostService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ModelMapper mapper;

    @Override
    public PostResponse createPost(PostRequest postRequest) {
        Post post = Post.builder()
                .title(postRequest.getTitle())
                .description(postRequest.getDescription())
                .content(postRequest.getContent())
                .build();
        post = postRepository.save(post);
        return mapper.map(post, PostResponse.class);
    }

    @Override
    public PostResponsePagination getAllPosts(int pageNo, int pageSiZe, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSiZe, sort);
        Page<Post> posts = postRepository.findAll(pageable);

        List<PostResponse> postResponses = posts.getContent()
                .stream()
                .map(post -> mapper.map(post, PostResponse.class)).collect(Collectors.toList());

        return PostResponsePagination.builder()
                .content(postResponses)
                .pageNo(posts.getNumber())
                .pageSize(posts.getSize())
                .totalElements(posts.getTotalElements())
                .totalPages(posts.getTotalPages())
                .last(posts.isLast())
                .build();
    }

    @Override
    public PostResponse getPostById(Long postId) {
        return mapper.map(getPostFromDB(postId), PostResponse.class);
    }

    @Override
    public MessageResponse deletePost(Long postId) {
        Post post = getPostFromDB(postId);
        postRepository.delete(post);
        return new MessageResponse(String.format("Post with id: %s was deleted", postId));
    }

    @Override
    public PostResponse updatePost(Long postId, PostRequest postRequest) {
        Post post = getPostFromDB(postId);
        post.setTitle(postRequest.getTitle());
        post.setDescription(postRequest.getDescription());
        post.setContent(postRequest.getContent());
        post = postRepository.save(post);
        return mapper.map(post, PostResponse.class);
    }


    private Post getPostFromDB(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Post with id: %s not found", postId)));
    }

}
