package com.michael.blog.service.impl;

import com.michael.blog.entity.Comment;
import com.michael.blog.entity.ImageData;
import com.michael.blog.entity.Post;
import com.michael.blog.entity.User;
import com.michael.blog.exception.payload.CommentNotFoundException;
import com.michael.blog.exception.payload.ImageNotFoundException;
import com.michael.blog.exception.payload.PostNotFoundException;
import com.michael.blog.payload.request.CommentRequest;
import com.michael.blog.payload.response.CommentResponse;
import com.michael.blog.payload.response.MessageResponse;
import com.michael.blog.repository.CommentRepository;
import com.michael.blog.repository.ImageDataRepository;
import com.michael.blog.repository.PostRepository;
import com.michael.blog.service.CommentService;
import com.michael.blog.service.UserService;
import com.michael.blog.utility.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final ModelMapper mapper;
    private final UserService userService;
    private final ImageUtils imageUtils;
    private final ImageDataRepository imageDataRepository;


    @Override
    public CommentResponse createComment(Long postId, CommentRequest commentRequest) {
        Post post = getPostFromDB(postId);
        User user = userService.getLoggedInUser();
        Comment comment = Comment.builder()
                .username(user.getUsername())
                .body(commentRequest.getBody())
                .post(post)
                .user(user)
                .likes(0)
                .imageUrlSet(new HashSet<>())
                .build();
        comment = commentRepository.save(comment);
        return mapper.map(comment, CommentResponse.class);
    }

    @Override
    public List<CommentResponse> getAllCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId)
                .stream()
                .map(comment -> mapper.map(comment, CommentResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponse getCommentById(Long postId, Long commentId) {
        Post post = getPostFromDB(postId);
        Comment comment = getCommentFromDB(commentId);
        isCommentBelongPost(post, comment);
        return mapper.map(comment, CommentResponse.class);
    }

    @Override
    public CommentResponse updateComment(Long postId, Long commentId, CommentRequest commentRequest) {
        Post post = getPostFromDB(postId);
        Comment comment = getCommentFromDB(commentId);
        isCommentBelongPost(post, comment);
        isCommentBelongUser(comment);
        comment.setBody(commentRequest.getBody());
        comment = commentRepository.save(comment);
        return mapper.map(comment, CommentResponse.class);
    }


    @Override
    public MessageResponse deleteComment(Long postId, Long commentId) {
        Post post = getPostFromDB(postId);
        Comment comment = getCommentFromDB(commentId);
        isCommentBelongPost(post, comment);
        isCommentBelongUser(comment);
        commentRepository.delete(comment);
        imageDataRepository.deleteAll(imageDataRepository.findAllByPostIdAndCommentId(postId, commentId));
        return new MessageResponse(String.format("Comment with id: %s was deleted", commentId));
    }

    @Override
    public Comment getCommentFromDB(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(String.format("Comment with id %s not found", commentId)));
    }

    @Override
    public void isCommentBelongPost(Post post, Comment comment) {
        if (!comment.getPost().getId().equals(post.getId())) {
            throw new RuntimeException("Comment does not belong to post");
        }
    }

    @Override
    public void isCommentBelongUser(Comment comment) {
        if (!comment.getUser().getId().equals(userService.getLoggedInUser().getId())) {
            throw new RuntimeException("This comment doesn't belong to you, you can't delete it!");
        }
    }

    @Override
    public CommentResponse likeComment(Long commentId) {
        User user = userService.getLoggedInUser();
        Comment comment = getCommentFromDB(commentId);

        Optional<String> userLiked = comment.getLikedUsers()
                .stream()
                .filter(u -> u.equals(user.getUsername()))
                .findAny();

        if (userLiked.isPresent()) {
            comment.setLikes(comment.getLikes() - 1);
            comment.getLikedUsers().remove(user.getUsername());
        } else {
            comment.setLikes(comment.getLikes() + 1);
            comment.getLikedUsers().add(user.getUsername());
        }
        comment = commentRepository.save(comment);
        return mapper.map(comment, CommentResponse.class);
    }

    @Override
    public CommentResponse addImageToComment(Long postId, Long commentId, List<MultipartFile> files) throws IOException {
        User user = userService.getLoggedInUser();
        getPostFromDB(postId);
        Comment comment = getCommentFromDB(commentId);
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("This comment doesn't belong to you, you can't add image");
        }
        if ((long) imageDataRepository.findAllByPostIdAndCommentId(comment.getPost().getId(), comment.getId()).size() == 2) {
            throw new IllegalStateException("Can not add more than 2 objects to the database");
        }

        comment = imageUtils.saveImagesToComment(user, comment, files);
        return mapper.map(comment, CommentResponse.class);
    }

    @Override
    public byte[] viewCommentImage(String username, Long postId, Long commentId, String filename) {
        userService.findUserByUsernameInDB(username);
        Post post = getPostFromDB(postId);
        Comment comment = getCommentFromDB(commentId);
        ImageData imageData = imageDataRepository
                .findByPostIdAndCommentIdAndFileName(post.getId(), comment.getId(), filename)
                .orElseThrow(() -> new ImageNotFoundException("Image Not Found"));

        return imageUtils.decompressImage(imageData.getData());
    }


    @Override
    public MessageResponse deleteCommentImage(Long postId, Long commentId, String filename) {
        User user = userService.getLoggedInUser();
        Post post = getPostFromDB(postId);
        Comment comment = getCommentFromDB(commentId);
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("This comment doesn't belong to you, you can't delete image");
        }
        ImageData imageData = imageDataRepository
                .findByPostIdAndCommentIdAndFileName(post.getId(), comment.getId(), filename)
                .orElseThrow(() -> new ImageNotFoundException("Image Not Found"));
        imageDataRepository.delete(imageData);
        comment.removeImageURL(imageData.getImageURL());
        commentRepository.save(comment);
        return new MessageResponse(String.format("Image with file name %s was deleted", filename));
    }

    @Override
    public MessageResponse deleteAllCommentImages(Long postId, Long commentId) {
        User user = userService.getLoggedInUser();
        getPostFromDB(postId);
        Comment comment = getCommentFromDB(commentId);
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("This comment doesn't belong to you, you can't delete image");
        }
        List<ImageData> imageDataList = imageDataRepository.findAllByPostIdAndCommentId(postId, commentId);

        if (!imageDataList.isEmpty()) {
            imageDataRepository.deleteAll(imageDataList);
            comment.getImageUrlSet().clear();
            commentRepository.save(comment);
            return new MessageResponse(String.format("All images from comment with id %s was deleted", commentId));
        }
        return new MessageResponse("No photo to delete");
    }

    private Post getPostFromDB(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(String.format("Post with id: %s not found", postId)));
    }

}
