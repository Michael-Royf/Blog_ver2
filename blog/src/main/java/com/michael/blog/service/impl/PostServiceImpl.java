package com.michael.blog.service.impl;

import com.michael.blog.entity.Category;
import com.michael.blog.entity.ImageData;
import com.michael.blog.entity.Post;
import com.michael.blog.entity.User;
import com.michael.blog.exception.payload.ImageNotFoundException;
import com.michael.blog.exception.payload.PostNotFoundException;
import com.michael.blog.exception.payload.UsernameExistException;
import com.michael.blog.payload.request.PostRequest;
import com.michael.blog.payload.response.MessageResponse;
import com.michael.blog.payload.response.PostResponse;
import com.michael.blog.payload.response.PostResponsePagination;
import com.michael.blog.repository.CategoryRepository;
import com.michael.blog.repository.ImageDataRepository;
import com.michael.blog.repository.PostRepository;
import com.michael.blog.service.PostService;
import com.michael.blog.service.UserService;
import com.michael.blog.utility.ImageUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.michael.blog.constants.FileConstant.IMAGE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final ModelMapper mapper;
    private final UserService userService;
    private final CategoryRepository categoryRepository;
    private final ImageUtils imageUtils;
    private final ImageDataRepository imageDataRepository;


    @Override
    public PostResponse createPost(PostRequest postRequest) {
        Category category = getCategoryFromDBById(postRequest.getCategoryId());
        User user = userService.getLoggedInUser();
        if (postRepository.findPostByTitle(postRequest.getTitle()).isPresent()) {
            throw new RuntimeException("Title already exists");
        }
        Post post = Post.builder()
                .username(user.getUsername())
                .title(postRequest.getTitle())
                .description(postRequest.getDescription())
                .content(postRequest.getContent())
                .user(user)
                .category(category)
                .likes(0)
                .imageUrlSet(new HashSet<>())
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
    public List<PostResponse> getMyPosts() {
        List<Post> posts = postRepository.findAllByUserId(userService.getLoggedInUser().getId());
        return posts.stream()
                .map(post -> mapper.map(post, PostResponse.class))
                .collect(Collectors.toList());
    }


    @Override
    public PostResponse getPostById(Long postId) {
        return mapper.map(getPostFromDB(postId), PostResponse.class);
    }

    @Override
    public MessageResponse deletePost(Long postId) {
        Post post = getPostFromDB(postId);
        isPostBelongUser(post);
        postRepository.delete(post);
        imageDataRepository.deleteAll(imageDataRepository.findAllByPostId(postId));
        return new MessageResponse(String.format("Post with id: %s was deleted", postId));
    }

    @Override
    public PostResponse updatePost(Long postId, PostRequest postRequest) {
        Post post = getPostFromDB(postId);
        isPostBelongUser(post);

        Category category = getCategoryFromDBById(postRequest.getCategoryId());
        post.setTitle(postRequest.getTitle());
        post.setDescription(postRequest.getDescription());
        post.setContent(postRequest.getContent());
        post.setCategory(category);
        post = postRepository.save(post);
        return mapper.map(post, PostResponse.class);
    }

    @Override
    public List<PostResponse> getPostsByCategory(Long categoryId) {
        getCategoryFromDBById(categoryId);
        return postRepository.findPostsByCategoryId(categoryId).stream()
                .map(post -> mapper.map(post, PostResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public void isPostBelongUser(Post post) {
        if (!post.getUser().getId().equals(userService.getLoggedInUser().getId())) {
            throw new RuntimeException("This post doesn't belong to you, you can't delete it!");
        }
    }

    @Override
    public PostResponse likePost(Long postId) {
        User user = userService.getLoggedInUser();
        Post post = getPostFromDB(postId);

        Optional<String> userLiked = post.getLikedUsers()
                .stream()
                .filter(u -> u.equals(user.getUsername()))
                .findAny();

        if (userLiked.isPresent()) {
            post.setLikes(post.getLikes() - 1);
            post.getLikedUsers().remove(user.getUsername());
        } else {
            post.setLikes(post.getLikes() + 1);
            post.getLikedUsers().add(user.getUsername());
        }
        post = postRepository.save(post);
        return mapper.map(post, PostResponse.class);
    }

    @Override
    public PostResponse addImageToPost(Long postId, List<MultipartFile> files) throws IOException {
        User user = userService.getLoggedInUser();
        Post post = getPostFromDB(postId);
        isPostBelongUser(post, user);

        if ((long) imageDataRepository.findAllByPostIdAndIsPostImage(post.getId(), true).size() == 5) {
            throw new IllegalStateException("Can not add more than 5 objects to the database");
        }

        imageUtils.saveImagesToPost(user, post, files);
        return mapper.map(post, PostResponse.class);
    }


    @Override
    public byte[] viewPostImage(String username, Long postId, String fileName) {
        userService.findUserByUsernameInDB(username);
        Post post = getPostFromDB(postId);

        ImageData imageData = imageDataRepository.findByFileNameAndPostId(fileName, post.getId())
                .orElseThrow(() -> new ImageNotFoundException(IMAGE_NOT_FOUND));
        return imageUtils.decompressImage(imageData.getData());
    }

    @Override
    public List<byte[]> viewAllPostImages(Long postId) {
        List<byte[]> photos = new ArrayList<>();
        imageDataRepository.findAllByPostId(postId)
                .forEach(photo -> photos.add(imageUtils.decompressImage(photo.getData())));
        return photos;
    }

    @Override
    public MessageResponse deletePostImage(Long postId, String filename) {
        User user = userService.getLoggedInUser();
        Post post = getPostFromDB(postId);
        isPostBelongUser(post, user);
        ImageData imageData = imageDataRepository.findByFileNameAndPostId(filename, postId)
                .orElseThrow(() -> new ImageNotFoundException(IMAGE_NOT_FOUND));
        post.removeImageURL(imageData.getImageURL());
        postRepository.save(post);
        imageDataRepository.delete(imageData);
        return new MessageResponse(String.format("Image with file name %s was deleted", filename));
    }

    @Override
    public MessageResponse deleteAllPostImages(Long postId) {
        User user = userService.getLoggedInUser();
        Post post = getPostFromDB(postId);
        isPostBelongUser(post, user);
        List<ImageData> imageDataList = imageDataRepository.findAllByPostIdAndIsPostImage(postId, true);
        if (!imageDataList.isEmpty()) {
            imageDataRepository.deleteAll(imageDataList);
            post.getImageUrlSet().clear();
            postRepository.save(post);
            return new MessageResponse(String.format("All images from post with id %s was deleted", post.getId()));
        } else {
            return new MessageResponse("No photo to delete");
        }

    }

    private void isPostBelongUser(Post post, User user) {
        if (!post.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("This post doesn't belong to you, you can't add or remove image");
        }
    }


    private Post getPostFromDB(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(String.format("Post with id: %s not found", postId)));
    }

    private Category getCategoryFromDBById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Category with id %d not found", categoryId)));
    }

}
